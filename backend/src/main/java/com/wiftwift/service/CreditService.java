package com.wiftwift.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import com.wiftwift.model.CreditNode;
import com.wiftwift.model.Expense;
import com.wiftwift.model.ExpenseParticipant;
import com.wiftwift.model.Group;
import com.wiftwift.repository.CreditRepository;
import com.wiftwift.repository.ExpenseParticipantRepository;
import com.wiftwift.repository.ExpenseRepository;
import com.wiftwift.repository.GroupRepository;
import com.wiftwift.repository.UserRepository;
import org.springframework.data.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class CreditService {
    @Autowired
    private CreditRepository creditRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private ExpenseRepository expenseRepository;
    @Autowired
    private ExpenseParticipantRepository expenseParticipantRepository;
    @Autowired
    private UserRepository userRepository; // Добавьте, если еще нет

    public List<CreditNode> getCreditsTo(Long userId) {
        return creditRepository.findByToUser(userId);
    }

    public List<CreditNode> getCreditsFrom(Long userId) {
        return creditRepository.findByFromUser(userId);
    }

    public CreditNode returnCredit(Long creditId, BigDecimal amount) {
        CreditNode credit = creditRepository.findById(creditId)
                .orElseThrow(() -> new RuntimeException("Credit not found"));
    
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }

        if (credit.getReturnedAmount() == null) {
            credit.setReturnedAmount(amount);
        } else {
            credit.setReturnedAmount(credit.getReturnedAmount().add(amount));
        }

        credit.setApproved(false);

        return creditRepository.save(credit);
    }

    public void approveCredit(Long creditId) {
        CreditNode credit = creditRepository.findById(creditId)
                .orElseThrow(() -> new RuntimeException("Credit not found"));

        if (credit.getReturnedAmount() != null) {
            credit.setCreditAmount(credit.getCreditAmount().subtract(credit.getReturnedAmount()));
            if (credit.getCreditAmount().compareTo(new BigDecimal(0)) != 1) {
                creditRepository.delete(credit);
                return;
            }
        }

        credit.setReturnedAmount(null);
        credit.setApproved(true);
        creditRepository.save(credit);
    }

    public void closeGroup(Long groupId) {
        List<Expense> expenses = expenseRepository.findByGroupId(groupId);
        Group group = groupRepository.getById(groupId);
        if (group.getClosedAt() != null) {
            throw new RuntimeException("group already closed");
        }
        if (group.getPaidBy() < 1) {
            throw new RuntimeException("Невыбран пользователь оплативший траты");
        }
        group.setClosedAt(java.time.LocalDateTime.now());
        groupRepository.save(group);

        for (Expense expense : expenses) {
            List<ExpenseParticipant> participants = expenseParticipantRepository.findByExpenseId(expense.getId());

            BigDecimal participantShare = expense.getValue()
                    .divide(BigDecimal.valueOf(participants.size()), 2, RoundingMode.HALF_UP);

            for (ExpenseParticipant participant : participants) {
                if (participant.getUserId().equals(group.getPaidBy())) {
                    continue;
                }

                CreditNode credit = new CreditNode();
                credit.setFromUser(participant.getUserId());
                credit.setToUser(group.getPaidBy());
                credit.setCreditAmount(participantShare);
                credit.setReturnedAmount(BigDecimal.ZERO);
                credit.setApproved(false);

                creditRepository.save(credit);

                var user = userRepository.getById(participant.getUserId());
                if (user.getCreditTotal() == null) {
                    user.setCreditTotal(participantShare);
                } else {
                    user.setCreditTotal(user.getCreditTotal().add(participantShare));
                }
                userRepository.save(user);

                user = userRepository.getById(group.getPaidBy());
                if (user.getDebtTotal() == null) {
                    user.setDebtTotal(participantShare);
                } else {
                    user.setDebtTotal(user.getDebtTotal().add(participantShare));
                }
                userRepository.save(user);
            }
        }

        optimizeCredits();
    }

    private void optimizeCredits() {
        List<CreditNode> allCredits = creditRepository.findByApproved(false);

        Map<Pair<Long, Long>, BigDecimal> debtMap = new HashMap<>();

        for (CreditNode credit : allCredits) {
            BigDecimal amount = credit.getCreditAmount().subtract(
                    credit.getReturnedAmount() != null ? credit.getReturnedAmount() : BigDecimal.ZERO);

            if (amount.compareTo(BigDecimal.ZERO) <= 0)
                continue;

            Pair<Long, Long> debtKey = Pair.of(credit.getFromUser(), credit.getToUser());
            debtMap.put(debtKey, debtMap.getOrDefault(debtKey, BigDecimal.ZERO).add(amount));
        }

        boolean optimized;
        do {
            optimized = false;

            for (Map.Entry<Pair<Long, Long>, BigDecimal> entry : new HashMap<>(debtMap).entrySet()) {
                Pair<Long, Long> forward = entry.getKey();
                Pair<Long, Long> backward = Pair.of(forward.getSecond(), forward.getFirst());

                if (debtMap.containsKey(backward)) {
                    BigDecimal forwardAmount = entry.getValue();
                    BigDecimal backwardAmount = debtMap.get(backward);

                    BigDecimal minAmount = forwardAmount.min(backwardAmount);

                    // Уменьшаем долги на минимальное значение
                    if (minAmount.compareTo(BigDecimal.ZERO) > 0) {
                        debtMap.put(forward, forwardAmount.subtract(minAmount));
                        debtMap.put(backward, backwardAmount.subtract(minAmount));
                        // Удаляем записи, если долг стал нулевым
                        if (debtMap.get(forward).compareTo(BigDecimal.ZERO) <= 0) {
                            debtMap.remove(forward);
                        }
                        if (debtMap.get(backward).compareTo(BigDecimal.ZERO) <= 0) {
                            debtMap.remove(backward);
                        }

                        optimized = true;
                        break;
                    }
                }
            }
        } while (optimized);

        for (CreditNode credit : allCredits) {
            creditRepository.delete(credit);
        }

        for (Map.Entry<Pair<Long, Long>, BigDecimal> entry : debtMap.entrySet()) {
            CreditNode newCredit = new CreditNode();
            newCredit.setFromUser(entry.getKey().getFirst());
            newCredit.setToUser(entry.getKey().getSecond());
            newCredit.setCreditAmount(entry.getValue());
            newCredit.setReturnedAmount(BigDecimal.ZERO);
            newCredit.setApproved(false);

            creditRepository.save(newCredit);
        }
    }
}
