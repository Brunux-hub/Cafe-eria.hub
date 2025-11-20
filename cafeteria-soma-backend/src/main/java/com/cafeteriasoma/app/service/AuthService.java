package com.cafeteriasoma.app.service;

import com.cafeteriasoma.app.dto.auth.*;
import com.cafeteriasoma.app.entity.Rol;
import com.cafeteriasoma.app.entity.Usuario;
import com.cafeteriasoma.app.repository.RolRepository;
import com.cafeteriasoma.app.repository.UsuarioRepository;
import com.cafeteriasoma.app.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * Servicio de autenticación: login y registro de usuarios.
 */
@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    /**
     * Autentica al usuario y devuelve un token JWT.
     */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        // Autenticar con Spring Security
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername().toLowerCase(),
                        request.getPassword()
                )
        );

        // Cargar usuario desde la BD
        Usuario usuario = usuarioRepository.findByCorreo(request.getUsername().toLowerCase())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Generar token JWT con claims adicionales
        UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getCorreo());
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", usuario.getRol().getNombre());
        claims.put("userId", usuario.getIdUsuario());
        
        String token = jwtUtil.generateToken(userDetails, claims);

        // Construir respuesta
        UserDto userDto = mapToUserDto(usuario);
        return AuthResponse.builder()
                .user(userDto)
                .token(token)
                .build();
    }

    /**
     * Registra un nuevo usuario (rol CLIENT por defecto).
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Validar que el correo no exista
        if (usuarioRepository.findByCorreo(request.getEmail().toLowerCase()).isPresent()) {
            throw new RuntimeException("El correo ya está registrado");
        }

        // Buscar el rol CLIENT (o crear si no existe)
        Rol rolCliente = rolRepository.findByNombre("CLIENT")
                .orElseGet(() -> {
                    Rol nuevoRol = new Rol();
                    nuevoRol.setNombre("CLIENT");
                    nuevoRol.setDescripcion("Cliente de la cafetería");
                    return rolRepository.save(nuevoRol);
                });

        // Crear usuario
        Usuario usuario = Usuario.builder()
                .nombre(request.getFullName())
                .correo(request.getEmail().toLowerCase())
                .contrasena(passwordEncoder.encode(request.getPassword()))
                .rol(rolCliente)
                .activo(true)
                .build();

        usuario = usuarioRepository.save(usuario);

        // Generar token
        UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getCorreo());
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", usuario.getRol().getNombre());
        claims.put("userId", usuario.getIdUsuario());
        
        String token = jwtUtil.generateToken(userDetails, claims);

        // Construir respuesta
        UserDto userDto = mapToUserDto(usuario);
        return AuthResponse.builder()
                .user(userDto)
                .token(token)
                .build();
    }

    private UserDto mapToUserDto(Usuario usuario) {
        return UserDto.builder()
                .id(usuario.getIdUsuario().toString())
                .username(usuario.getCorreo())
                .email(usuario.getCorreo())
                .fullName(usuario.getNombre())
                .role(usuario.getRol().getNombre())
                .createdAt(usuario.getFechaCreacion())
                .build();
    }
}
