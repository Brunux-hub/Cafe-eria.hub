package com.cafeteriasoma.app.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO público de usuario (sin contraseña).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private String id;
    private String username;
    private String email;
    private String fullName;
    private String role;
    private LocalDateTime createdAt;
}
