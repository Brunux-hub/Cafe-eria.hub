package com.cafeteriasoma.app.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta de autenticación (login o register).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private UserDto user;
    private String token;
}
