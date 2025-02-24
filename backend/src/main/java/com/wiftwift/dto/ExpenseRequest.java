package com.wiftwift.dto;

import lombok.Data;

@Data
public class ExpenseRequest {
    private String name;
    private Long value;
    private Long groupId;
}

