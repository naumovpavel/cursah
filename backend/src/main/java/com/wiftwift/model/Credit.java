package com.wiftwift.model;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class Credit {
    private String relationshipId;
    private Long fromUser;
    private Long toUser;
    private BigDecimal creditAmount;
    private BigDecimal returnedAmount;
    private Boolean approved;
}
