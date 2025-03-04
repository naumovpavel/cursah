package com.wiftwift.service;

import com.wiftwift.dto.GroupResponse;
import com.wiftwift.dto.InviteRequest;
import com.wiftwift.model.Group;
import com.wiftwift.model.GroupParticipants;
import com.wiftwift.model.Invite;
import com.wiftwift.model.User;
import com.wiftwift.repository.GroupParticipantsRepository;
import com.wiftwift.repository.GroupRepository;
import com.wiftwift.repository.InviteRepository;
import com.wiftwift.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private InviteRepository inviteRepository;
    @Autowired
    private GroupParticipantsRepository groupParticipantsRepository;


    public Group createGroup(Group group, User currentUser) {
        group = groupRepository.save(group);
        addParticipant(group.getId(), currentUser.getUsername());
        return group;
    }

    public Object setPaymentSuggestion(Long groupId) {
        return groupRepository.findUserForPayment(groupId);
    }

    public void closeGroup(Long groupId) {
        Group group = groupRepository.findById(groupId).orElse(null);
        if (group != null) {
            group.setClosedAt(java.time.LocalDateTime.now());
            groupRepository.save(group);
        }
    }

    public void invitePerson(InviteRequest request) {
        Group group = groupRepository.findById(request.getGroupId()).orElseThrow(
                () -> new RuntimeException("группа не найдена")
        );
        User invitedUser = userRepository.findById(request.getToUser()).orElseThrow(
                () -> new RuntimeException("человек не найден")
        );
        Invite invite = new Invite();
        invite.setGroupId(group.getId());
        invite.setInvitedUser(invitedUser.getUsername());
        invite.setFromUser(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
        inviteRepository.save(invite);
    }

    public void setPaidBy(Long groupId, Long userId) {
        Group group = groupRepository.findById(groupId).orElseThrow(
                () -> new RuntimeException("группа не найдена")
        );
        userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("человек не найден")
        );
        group.setPaidBy(userId);
        groupRepository.save(group);
    }

    public void addParticipant(Long groupId, String invitedUser) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        User user = userRepository.findByUsername(invitedUser)
                .orElseThrow(() -> new RuntimeException("User not found"));

        GroupParticipants groupParticipants = new GroupParticipants();
        groupParticipants.setGroupId(group.getId());
        groupParticipants.setUserId(user.getId());
        groupParticipantsRepository.save(groupParticipants);
    }

    public GroupResponse findById(Long groupId) {

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        var res = new GroupResponse();
        res.setGroup(group);
        res.setUsers(groupParticipantsRepository.findUserByGroupId(groupId));
        return res;
    }

}
