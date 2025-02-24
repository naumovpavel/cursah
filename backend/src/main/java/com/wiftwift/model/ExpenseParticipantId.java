package com.wiftwift.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseParticipantId implements Serializable {
    private Long expenseId;
    private Long userId;
}

