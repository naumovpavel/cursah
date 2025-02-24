package com.wiftwift.service;

import com.wiftwift.dto.ExpenseRequest;
import com.wiftwift.model.Expense;
import com.wiftwift.model.ExpenseParticipant;
import com.wiftwift.model.ExpenseParticipantId;
import com.wiftwift.repository.ExpenseParticipantRepository;
import com.wiftwift.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private ExpenseParticipantRepository expenseParticipantRepository;
    @Autowired
    private UserService userService;

    public Expense addExpense(ExpenseRequest request) {
        Expense expense = new Expense();
        expense.setName(request.getName());
        expense.setValue(new BigDecimal(request.getValue()));
        expense.setGroupId(request.getGroupId());
        return expenseRepository.save(expense);
    }

    public void joinExpense(Long expenseId, Long userId) {
        if (userId == null) {
            userId = userService.getCurrentUserId();
        }
        ExpenseParticipantId id = new ExpenseParticipantId(expenseId, userId);

        ExpenseParticipant expenseParticipant = new ExpenseParticipant();
        expenseParticipant.setId(id);
        expenseParticipant.setExpense(expenseRepository.findById(expenseId).orElseThrow(
                () -> new IllegalArgumentException("Expense not found")
        ));
        expenseParticipant.setConfirmed(userId == null);

        expenseParticipant.setUser(userService.findById(userId));
        expenseParticipantRepository.save(expenseParticipant);
    }


    public void confirm(Long expenseId) {
        ExpenseParticipant e = expenseParticipantRepository.findById(new ExpenseParticipantId(expenseId, userService.getCurrentUserId())).orElseThrow(
                () -> new IllegalArgumentException("Expense not found")
        );
        e.setConfirmed(true);
        expenseParticipantRepository.save(e);
    }

    public List<Expense> getExpensesBiGroupId(Long groupId) {
        return expenseRepository.findByGroupId(groupId);
    }
}
