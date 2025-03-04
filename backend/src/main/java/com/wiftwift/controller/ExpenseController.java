package com.wiftwift.controller;

import com.wiftwift.dto.ExpenseParticipantResponse;
import com.wiftwift.dto.ExpenseRequest;
import com.wiftwift.dto.MyCreditsDTO.CreditInfo;
import com.wiftwift.model.Expense;
import com.wiftwift.model.ExpenseParticipant;
import com.wiftwift.model.User;
import com.wiftwift.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @PostMapping
    public Expense add(@RequestBody ExpenseRequest request) {
        return expenseService.addExpense(request);
    }

    @PostMapping("/{expenseId}/join")
    public String join(@PathVariable Long expenseId,
                       @RequestParam(required = false) Long userId) {
        expenseService.joinExpense(expenseId, userId);
        return "Участие добавлено";
    }

    @PostMapping("/{expenseId}/confirm")
    public String confirm(@PathVariable Long expenseId) {
        expenseService.confirm(expenseId);
        return "Исполнение долга подтверждено";
    }

    @PostMapping("/{expenseId}/reject")
    public String reject(@PathVariable Long expenseId) {
        expenseService.reject(expenseId);
        return "Отклоненно";
    }

    @GetMapping("/{expenseId}/participants")
    public List<ExpenseParticipantResponse> participants(@PathVariable Long expenseId) {
        var participants = expenseService.participants(expenseId).stream().map(p -> new ExpenseParticipantResponse(p.getUserId(), p.getExpenseId(), p.getConfirmed())).toList();
        System.out.println(participants);
        return participants;
    }
}