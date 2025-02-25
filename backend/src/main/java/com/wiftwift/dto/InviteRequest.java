package com.wiftwift.dto;

import lombok.Data;

@Data
public class InviteRequest {
    private Long groupId;
    private Long toUser;
}
