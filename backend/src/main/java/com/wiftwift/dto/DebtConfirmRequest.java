package com.wiftwift.dto;

import lombok.Data;

@Data
public class DebtConfirmRequest {
    private Long fromUser;
    private Long toUser;
    private String amount;
}
