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

    @Autowired
    private SessionService sessionService;

    /**
     * Autentica al usuario y devuelve un token JWT.
     * Almacena el token en Redis para validación de sesión.
     */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        System.out.println("========== AuthService.login ==========");
        System.out.println("Username recibido: " + request.getUsername());
        System.out.println("Username lowercase: " + request.getUsername().toLowerCase());
        
        try {
            // Autenticar con Spring Security
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername().toLowerCase(),
                            request.getPassword()
                    )
            );
            System.out.println("Autenticación exitosa con AuthenticationManager");
        } catch (Exception e) {
            System.err.println("ERROR en autenticación: " + e.getMessage());
            throw e;
        }

        // Cargar usuario desde la BD
        Usuario usuario = usuarioRepository.findByCorreo(request.getUsername().toLowerCase())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Validar que el usuario esté activo
        if (!usuario.getActivo()) {
            throw new RuntimeException("Usuario inactivo. Contacte al administrador.");
        }

        // Generar token JWT con claims adicionales
        UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getCorreo());
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", usuario.getRol().getNombre());
        claims.put("userId", usuario.getIdUsuario());
        
        String token = jwtUtil.generateToken(userDetails, claims);

        // Almacenar token en Redis
        sessionService.storeToken(usuario.getCorreo(), token);
        
        // Almacenar información adicional de sesión
        sessionService.storeSessionData(usuario.getCorreo(), "lastLogin", java.time.LocalDateTime.now().toString());
        sessionService.storeSessionData(usuario.getCorreo(), "role", usuario.getRol().getNombre());

        // Construir respuesta
        UserDto userDto = mapToUserDto(usuario);
        return AuthResponse.builder()
                .user(userDto)
                .token(token)
                .build();
    }

    /**
     * Cierra la sesión del usuario invalidando el token en Redis.
     */
    @Transactional(readOnly = true)
    public void logout(String username) {
        sessionService.invalidateToken(username);
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
