package com.cafeteriasoma.app.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para solicitud de registro de usuario.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    @NotBlank(message = "El nombre completo es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String fullName;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Debe proporcionar un correo válido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    private String phone;
    private String address;
}
