package com.wiftwift.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class GroupRequest {
    @Schema(description = "название")
    private String name;
    @Schema(description = "описание")
    private String description;
    @Schema(description = "описание")
    private Long paidBy;
}
