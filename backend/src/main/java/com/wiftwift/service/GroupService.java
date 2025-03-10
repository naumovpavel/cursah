package com.wiftwift.service;

import com.wiftwift.dto.GetAcknowledgmentsResponse;
import com.wiftwift.dto.GroupResponse;
import com.wiftwift.dto.InviteRequest;
import com.wiftwift.exception.ConflictException;
import com.wiftwift.exception.NotFoundException;
import com.wiftwift.model.Expense;
import com.wiftwift.model.ExpenseParticipant;
import com.wiftwift.model.Group;
import com.wiftwift.model.GroupParticipants;
import com.wiftwift.model.Invite;
import com.wiftwift.model.User;
import com.wiftwift.model.UserNode;
import com.wiftwift.repository.ExpenseParticipantRepository;
import com.wiftwift.repository.ExpenseRepository;
import com.wiftwift.repository.GroupParticipantsRepository;
import com.wiftwift.repository.GroupRepository;
import com.wiftwift.repository.InviteRepository;
import com.wiftwift.repository.UserRepository;

import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private InviteRepository inviteRepository;
    @Autowired
    private GroupParticipantsRepository groupParticipantsRepository;
    @Autowired
    private ExpenseRepository expenseRepository;
    @Autowired
    private ExpenseParticipantRepository expenseParticipantRepository;


    public Group createGroup(Group group, User currentUser) {
        group = groupRepository.save(group);
        addParticipant(group.getId(), currentUser.getUsername());
        return group;
    }

    public Object setPaymentSuggestion(Long groupId) {
        return groupRepository.findUserForPayment(groupId);
    }

    public Group closeGroup(Long groupId) {
        Group group = groupRepository.getById(groupId);
        if (group.getClosedAt() != null) {
            throw new ConflictException("группа уже закрыта");
        }
        if (group.getPaidBy() < 1) {
            throw new ConflictException("Невыбран пользователь оплативший траты");
        }
        group.setClosedAt(java.time.LocalDateTime.now());
        groupRepository.save(group);

        List<Expense> expenses = expenseRepository.findByGroupId(group.getId());

        for (Expense expense : expenses) {
            List<ExpenseParticipant> participants = expenseParticipantRepository.findByExpenseId(expense.getId());

            int participantCount = 0;
            for (ExpenseParticipant participant : participants) {
                if (participant.getConfirmed()) {
                    participantCount++;
                }
            }

            BigDecimal participantShare = expense.getValue().divide(BigDecimal.valueOf(participantCount), 2, RoundingMode.HALF_UP);

            for (ExpenseParticipant participant : participants) {
                if (participant.getUserId().equals(group.getPaidBy())) {
                    continue;
                }

                if (!participant.getConfirmed()) {
                    continue;
                }

                var user = userRepository.getById(participant.getUserId());
                if (user.getCreditTotal() == null) {
                    user.setCreditTotal(participantShare);
                } else {
                    user.setCreditTotal(user.getCreditTotal().add(participantShare));
                }
                userRepository.save(user);
            }

            var user = userRepository.getById(group.getPaidBy());
            if (user.getDebtTotal() == null) {
                user.setDebtTotal(participantShare.multiply(BigDecimal.valueOf(participantCount)));
            } else {
                user.setDebtTotal(user.getDebtTotal().add(participantShare.multiply(BigDecimal.valueOf(participantCount))));
            }
            userRepository.save(user);
        }

        return group;
    }

    public void openGroup(Long groupId) {
        Group group = groupRepository.getById(groupId);

        List<Expense> expenses = expenseRepository.findByGroupId(group.getId());

        for (Expense expense : expenses) {
            List<ExpenseParticipant> participants = expenseParticipantRepository.findByExpenseId(expense.getId());

            int participantCount = 0;
            for (ExpenseParticipant participant : participants) {
                if (participant.getConfirmed()) {
                    participantCount++;
                }
            }

            BigDecimal participantShare = expense.getValue().divide(BigDecimal.valueOf(participantCount), 2, RoundingMode.HALF_UP);

            for (ExpenseParticipant participant : participants) {
                if (participant.getUserId().equals(group.getPaidBy())) {
                    continue;
                }

                if (!participant.getConfirmed()) {
                    continue;
                }

                var user = userRepository.getById(participant.getUserId());
                if (user.getCreditTotal() != null) {
                    user.setCreditTotal(user.getCreditTotal().subtract(participantShare));
                }
                userRepository.save(user);
            }

            var user = userRepository.getById(group.getPaidBy());
            if (user.getDebtTotal() != null) {
                user.setDebtTotal(user.getDebtTotal().subtract(participantShare.multiply(BigDecimal.valueOf(participantCount))));
            }
            userRepository.save(user);
        }

        group.setClosedAt(null);
        groupRepository.save(group);
    }

    public void invitePerson(InviteRequest request) {
        Group group = groupRepository.findById(request.getGroupId()).orElseThrow(
                () -> new NotFoundException("группа не найдена")
        );
        User invitedUser = userRepository.findById(request.getToUser()).orElseThrow(
                () -> new NotFoundException("человек не найден")
        );
        Invite invite = new Invite();
        invite.setGroupId(group.getId());
        invite.setInvitedUser(invitedUser.getUsername());
        invite.setFromUser(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
        inviteRepository.save(invite);
    }

    public void setPaidBy(Long groupId, Long userId) {
        Group group = groupRepository.findById(groupId).orElseThrow(
                () -> new NotFoundException("группа не найдена")
        );
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("человек не найден")
        );
        group.setPaidBy(userId);
        groupRepository.save(group);
    }

    public void addParticipant(Long groupId, String invitedUser) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("группа не найдена"));

        User user = userRepository.findByUsername(invitedUser)
                .orElseThrow(() -> new NotFoundException("User not found"));

        GroupParticipants groupParticipants = new GroupParticipants();
        groupParticipants.setGroupId(group.getId());
        groupParticipants.setUserId(user.getId());
        groupParticipantsRepository.save(groupParticipants);
    }

    public GroupResponse findById(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("группа не найдена"));
        var res = new GroupResponse();
        res.setGroup(group);
        res.setUsers(groupParticipantsRepository.findUserByGroupId(groupId));
        return res;
    }

    public Boolean setAcknowledgmentAllExpenses(Long groupId, Long userId) {
        Optional<GroupParticipants> optionalParticipant = groupParticipantsRepository
                .GroupParticipantsByUserIdAndGroupId(userId, groupId);
        
        if (optionalParticipant.isPresent()) {
            GroupParticipants participant = optionalParticipant.get();
            participant.setAcknowledgmentAllExpenses(true);
            groupParticipantsRepository.save(participant);
            
            return checkAndCloseGroupIfAllAcknowledged(groupId);
        } else {
            throw new ConflictException("Пользователь не является участником группы");
        }
    }
    
    public Boolean setAcknowledgmentAllExpenseParticipantense(Long groupId, Long userId) {
        Optional<GroupParticipants> optionalParticipant = groupParticipantsRepository
                .GroupParticipantsByUserIdAndGroupId(userId, groupId);
        
        if (optionalParticipant.isPresent()) {
            GroupParticipants participant = optionalParticipant.get();
            participant.setAcknowledgmentAllExpenseParticipantense(true);
            groupParticipantsRepository.save(participant);
            
            return checkAndCloseGroupIfAllAcknowledged(groupId);
        } else {
            throw new ConflictException("Пользователь не является участником группы");
        }
    }

    public GetAcknowledgmentsResponse getAcknowledgments(Long groupId, Long userId) {
        Optional<GroupParticipants> optionalParticipant = groupParticipantsRepository
                .GroupParticipantsByUserIdAndGroupId(userId, groupId);
        
        if (optionalParticipant.isPresent()) {
            GroupParticipants participant = optionalParticipant.get();
            return new GetAcknowledgmentsResponse(participant.getAcknowledgmentAllExpenses(), participant.getAcknowledgmentAllExpenseParticipantense());
        } else {
            throw new ConflictException("Пользователь не является участником группы");
        }
    }
    
    private Boolean checkAndCloseGroupIfAllAcknowledged(Long groupId) {
        List<User> groupUsers = groupParticipantsRepository.findUserByGroupId(groupId);
        boolean allAcknowledged = true;
        
        for (User user : groupUsers) {
            Optional<GroupParticipants> optParticipant = groupParticipantsRepository
                    .GroupParticipantsByUserIdAndGroupId(user.getId(), groupId);
            
            if (optParticipant.isPresent()) {
                GroupParticipants participant = optParticipant.get();
                
                if (participant.getAcknowledgmentAllExpenses() == null || 
                    !participant.getAcknowledgmentAllExpenses() || 
                    participant.getAcknowledgmentAllExpenseParticipantense() == null || 
                    !participant.getAcknowledgmentAllExpenseParticipantense()) {
                    
                    allAcknowledged = false;
                    break;
                }
            } else {
                allAcknowledged = false;
                break;
            }
        }

        return allAcknowledged;
    }
}
