package com.cafeteriasoma.app.service;

import com.cafeteriasoma.app.entity.Usuario;
import com.cafeteriasoma.app.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;

/**
 * Servicio para cargar usuarios desde la base de datos para Spring Security.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Buscar usuario por correo (normalizado a minúsculas)
        Usuario usuario = usuarioRepository.findByCorreo(username.toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        if (!usuario.getActivo()) {
            throw new UsernameNotFoundException("Usuario inactivo: " + username);
        }

        return new org.springframework.security.core.userdetails.User(
                usuario.getCorreo(),
                usuario.getContrasena(),
                getAuthorities(usuario)
        );
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Usuario usuario) {
        String roleName = "ROLE_" + usuario.getRol().getNombre().toUpperCase();
        return Collections.singletonList(new SimpleGrantedAuthority(roleName));
    }
}
