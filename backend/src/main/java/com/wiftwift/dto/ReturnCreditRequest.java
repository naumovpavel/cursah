package com.wiftwift.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ReturnCreditRequest {
    private BigDecimal amount;
    private Long creditId;
}

