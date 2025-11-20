package com.cafeteriasoma.app.controller;

import com.cafeteriasoma.app.entity.Usuario;
import com.cafeteriasoma.app.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador REST para gestión de usuarios.
 * Endpoint base: /api/usuarios
 */
@RestController
@RequestMapping("/api/usuarios")
@PreAuthorize("hasRole('ADMIN')")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * GET /api/usuarios
     * Obtiene todos los usuarios (solo ADMIN)
     */
    @GetMapping
    public ResponseEntity<List<Usuario>> getAllUsuarios() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        return ResponseEntity.ok(usuarios);
    }

    /**
     * GET /api/usuarios/activos
     * Obtiene solo usuarios activos (solo ADMIN)
     */
    @GetMapping("/activos")
    public ResponseEntity<List<Usuario>> getUsuariosActivos() {
        List<Usuario> usuarios = usuarioRepository.findByActivoTrue();
        return ResponseEntity.ok(usuarios);
    }

    /**
     * GET /api/usuarios/{id}
     * Obtiene un usuario por ID (solo ADMIN)
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUsuarioById(@PathVariable Long id) {
        return usuarioRepository.findById(id)
                .map(usuario -> {
                    // No enviar la contraseña en la respuesta
                    usuario.setContrasena(null);
                    return ResponseEntity.ok(usuario);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/usuarios/email/{email}
     * Obtiene un usuario por email (solo ADMIN)
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<?> getUsuarioByEmail(@PathVariable String email) {
        return usuarioRepository.findByCorreo(email)
                .map(usuario -> {
                    usuario.setContrasena(null);
                    return ResponseEntity.ok(usuario);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/usuarios
     * Crea un nuevo usuario (solo ADMIN)
     */
    @PostMapping
    public ResponseEntity<?> createUsuario(@RequestBody Usuario usuario) {
        try {
            // Verificar que el email no exista
            if (usuarioRepository.findByCorreo(usuario.getCorreo()).isPresent()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "El correo electrónico ya está registrado"));
            }
            
            // Encriptar contraseña
            usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
            
            if (usuario.getActivo() == null) {
                usuario.setActivo(true);
            }
            
            Usuario savedUsuario = usuarioRepository.save(usuario);
            savedUsuario.setContrasena(null); // No devolver la contraseña
            
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUsuario);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * PUT /api/usuarios/{id}
     * Actualiza un usuario existente (solo ADMIN)
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUsuario(@PathVariable Long id, @RequestBody Usuario usuario) {
        return usuarioRepository.findById(id)
                .map(existing -> {
                    existing.setNombre(usuario.getNombre());
                    existing.setCorreo(usuario.getCorreo());
                    existing.setTelefono(usuario.getTelefono());
                    existing.setDireccion(usuario.getDireccion());
                    existing.setActivo(usuario.getActivo());
                    
                    // Solo actualizar contraseña si se proporciona una nueva
                    if (usuario.getContrasena() != null && !usuario.getContrasena().isEmpty()) {
                        existing.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
                    }
                    
                    // Actualizar rol si se proporciona
                    if (usuario.getRol() != null) {
                        existing.setRol(usuario.getRol());
                    }
                    
                    Usuario updated = usuarioRepository.save(existing);
                    updated.setContrasena(null);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * DELETE /api/usuarios/{id}
     * Desactiva un usuario (soft delete) (solo ADMIN)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUsuario(@PathVariable Long id) {
        return usuarioRepository.findById(id)
                .map(usuario -> {
                    usuario.setActivo(false);
                    usuarioRepository.save(usuario);
                    return ResponseEntity.ok(Map.of("message", "Usuario desactivado exitosamente"));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * PATCH /api/usuarios/{id}/activar
     * Activa un usuario desactivado (solo ADMIN)
     */
    @PatchMapping("/{id}/activar")
    public ResponseEntity<?> activarUsuario(@PathVariable Long id) {
        return usuarioRepository.findById(id)
                .map(usuario -> {
                    usuario.setActivo(true);
                    usuarioRepository.save(usuario);
                    return ResponseEntity.ok(Map.of("message", "Usuario activado exitosamente"));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * PATCH /api/usuarios/{id}/cambiar-password
     * Cambia la contraseña de un usuario (solo ADMIN)
     */
    @PatchMapping("/{id}/cambiar-password")
    public ResponseEntity<?> cambiarPassword(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        
        String nuevaPassword = request.get("password");
        
        if (nuevaPassword == null || nuevaPassword.length() < 6) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "La contraseña debe tener al menos 6 caracteres"));
        }
        
        return usuarioRepository.findById(id)
                .map(usuario -> {
                    usuario.setContrasena(passwordEncoder.encode(nuevaPassword));
                    usuarioRepository.save(usuario);
                    return ResponseEntity.ok(Map.of("message", "Contraseña actualizada exitosamente"));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
