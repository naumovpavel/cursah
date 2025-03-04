package com.wiftwift.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ExpenseParticipant {
    @EmbeddedId
    private ExpenseParticipantId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @MapsId("userId")
    private User user;

    @Column(name = "user_id", insertable = false, updatable = false)
    private Long userId;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_id")
    @MapsId("expenseId")
    private Expense expense;


    @Column(name = "expense_id", insertable = false, updatable = false)
    private Long expenseId;

    private Boolean confirmed;
}
