package com.wiftwift.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreditDTO {
    // @JsonProperty(value = "id")
    private String id;
    private Long fromUser;
    private Long toUser;
    private BigDecimal creditAmount;
    private BigDecimal returnedAmount;
    private Boolean approved;
}

