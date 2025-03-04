package com.wiftwift.dto;

import com.wiftwift.model.Expense;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExpenseParticipantResponse {
    private Long userId;
    private Long expenseId;
    private boolean confirmed;
}
