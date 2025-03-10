package com.wiftwift.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetAcknowledgmentsResponse {
    private Boolean expense;
    private Boolean expenseParticipants;
}
