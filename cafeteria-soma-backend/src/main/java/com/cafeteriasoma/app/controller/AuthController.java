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
 * Controlador REST para autenticación (login, register, logout).
 * Endpoint base: /api/auth
 * 
 * CORS configurado a nivel global en SecurityConfig.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private com.cafeteriasoma.app.service.SessionService sessionService;

    @Autowired
    private WebSocketController webSocketController;

    /**
     * POST /api/auth/login
     * Autentica usuario y devuelve token JWT.
     * El token se almacena en Redis para validación de sesión.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            
            // Notificar conexión por WebSocket
            java.util.Map<String, String> notification = new java.util.HashMap<>();
            notification.put("username", request.getUsername());
            webSocketController.userConnect(notification);
            
            return ResponseEntity.ok(response);
        } catch (org.springframework.security.core.AuthenticationException e) {
            return ResponseEntity.status(401)
                    .body(java.util.Map.of(
                            "error", "Unauthorized",
                            "message", "Credenciales inválidas. Verifica tu email y contraseña."
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(java.util.Map.of(
                            "error", "Internal Server Error",
                            "message", e.getMessage()
                    ));
        }
    }

    /**
     * POST /api/auth/register
     * Registra nuevo usuario con rol CLIENT.
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(400)
                    .body(java.util.Map.of(
                            "error", "Bad Request",
                            "message", e.getMessage()
                    ));
        }
    }

    /**
     * POST /api/auth/logout
     * Invalida el token y cierra la sesión.
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody java.util.Map<String, String> request) {
        try {
            String username = request.get("username");
            authService.logout(username);
            
            // Notificar desconexión por WebSocket
            webSocketController.userDisconnect(request);
            
            return ResponseEntity.ok(java.util.Map.of(
                    "message", "Sesión cerrada exitosamente"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(java.util.Map.of(
                            "error", "Internal Server Error",
                            "message", e.getMessage()
                    ));
        }
    }

    /**
     * GET /api/auth/active-users
     * Devuelve el número de usuarios activos.
     */
    @GetMapping("/active-users")
    public ResponseEntity<?> getActiveUsers() {
        try {
            Long activeUsers = sessionService.countActiveUsers();
            return ResponseEntity.ok(java.util.Map.of(
                    "activeUsers", activeUsers,
                    "users", sessionService.getActiveUsers()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(java.util.Map.of(
                            "error", "Internal Server Error",
                            "message", e.getMessage()
                    ));
        }
    }
}
