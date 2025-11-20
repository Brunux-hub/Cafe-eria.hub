package com.cafeteriasoma.app.controller;

import com.cafeteriasoma.app.dto.auth.AuthResponse;
import com.cafeteriasoma.app.dto.auth.LoginRequest;
import com.cafeteriasoma.app.dto.auth.RegisterRequest;
import com.cafeteriasoma.app.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para autenticación (login y register).
 * Endpoint base: /api/auth
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException("Credenciales inválidas");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
