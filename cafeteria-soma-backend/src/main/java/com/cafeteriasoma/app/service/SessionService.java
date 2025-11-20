package com.cafeteriasoma.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Servicio para gestionar sesiones de usuario con Redis.
 * Almacena tokens activos, usuarios conectados y datos de sesión.
 */
@Service
public class SessionService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String SESSION_PREFIX = "cafeteria:session:";
    private static final String TOKEN_PREFIX = "cafeteria:token:";
    private static final String ACTIVE_USERS = "cafeteria:active_users";
    private static final long SESSION_TIMEOUT = 86400; // 24 horas en segundos

    /**
     * Almacena un token de sesión asociado al usuario
     */
    public void storeToken(String username, String token) {
        String key = TOKEN_PREFIX + username;
        redisTemplate.opsForValue().set(key, token, SESSION_TIMEOUT, TimeUnit.SECONDS);
        
        // Agregar usuario a la lista de usuarios activos
        redisTemplate.opsForSet().add(ACTIVE_USERS, username);
    }

    /**
     * Valida si un token es válido y existe en Redis
     */
    public boolean isTokenValid(String username, String token) {
        String key = TOKEN_PREFIX + username;
        Object storedToken = redisTemplate.opsForValue().get(key);
        return storedToken != null && storedToken.equals(token);
    }

    /**
     * Invalida el token del usuario (logout)
     */
    public void invalidateToken(String username) {
        String key = TOKEN_PREFIX + username;
        redisTemplate.delete(key);
        
        // Remover de usuarios activos
        redisTemplate.opsForSet().remove(ACTIVE_USERS, username);
    }

    /**
     * Almacena información adicional de la sesión
     */
    public void storeSessionData(String username, String key, Object value) {
        String sessionKey = SESSION_PREFIX + username + ":" + key;
        redisTemplate.opsForValue().set(sessionKey, value, SESSION_TIMEOUT, TimeUnit.SECONDS);
    }

    /**
     * Obtiene información de la sesión
     */
    public Object getSessionData(String username, String key) {
        String sessionKey = SESSION_PREFIX + username + ":" + key;
        return redisTemplate.opsForValue().get(sessionKey);
    }

    /**
     * Obtiene todos los usuarios activos
     */
    public Set<Object> getActiveUsers() {
        return redisTemplate.opsForSet().members(ACTIVE_USERS);
    }

    /**
     * Cuenta usuarios activos
     */
    public Long countActiveUsers() {
        return redisTemplate.opsForSet().size(ACTIVE_USERS);
    }

    /**
     * Renueva la expiración de la sesión
     */
    public void renewSession(String username) {
        String key = TOKEN_PREFIX + username;
        redisTemplate.expire(key, SESSION_TIMEOUT, TimeUnit.SECONDS);
    }
}
