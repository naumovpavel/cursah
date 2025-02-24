package com.wiftwift.dto;

import com.wiftwift.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class JwtAuthenticationResponse {
    private Long id;
    private String username;
    private String token;
    private Role role;
}
