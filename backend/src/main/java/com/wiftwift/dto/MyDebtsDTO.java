package com.wiftwift.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data

public class MyDebtsDTO {
    private List<DebtInfo> debts;

    @Data
    @AllArgsConstructor
    public static class DebtInfo {
        private Long userId;
        private BigDecimal amount;
    }

    public MyDebtsDTO(List<Map> results) {
        this.debts = results.stream()
                .map(map -> new DebtInfo(
                        Long.parseLong(((String) map.get("to")).replace("users/", "")),
                        new BigDecimal(map.get("amount").toString())
                ))
                .toList();
    }
}
