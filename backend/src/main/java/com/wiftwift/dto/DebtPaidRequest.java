package com.wiftwift.dto;

import lombok.Data;

@Data
public class DebtPaidRequest {
    private Long fromUser;
    private Long toUser;
    private String amount;
}
