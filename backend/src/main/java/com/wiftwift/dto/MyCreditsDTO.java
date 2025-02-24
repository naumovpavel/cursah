package com.wiftwift.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class MyCreditsDTO {
    private List<CreditInfo> credits;

    @Data
    @AllArgsConstructor
    public static class CreditInfo {
        private Long userId;
        private BigDecimal amount;
    }

    public MyCreditsDTO(List<Map> results) {
        this.credits = results.stream()
                .map(map -> new CreditInfo(
                        Long.parseLong(((String) map.get("from")).replace("users/", "")),
                        new BigDecimal(map.get("amount").toString())
                ))
                .toList();
    }
}
