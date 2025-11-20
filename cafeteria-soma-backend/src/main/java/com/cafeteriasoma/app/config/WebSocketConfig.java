package com.cafeteriasoma.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuración de WebSocket con STOMP para comunicación en tiempo real.
 * Permite notificaciones en tiempo real sobre:
 * - Nuevas ventas
 * - Cambios en inventario
 * - Usuarios conectados
 * - Actualizaciones de pedidos
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Habilita un broker simple en memoria para mensajes broadcast
        config.enableSimpleBroker("/topic", "/queue");
        
        // Prefijo para mensajes desde el cliente al servidor
        config.setApplicationDestinationPrefixes("/app");
        
        // Prefijo para mensajes dirigidos a usuarios específicos
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint para conexión WebSocket con fallback a SockJS
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("http://localhost*", "http://127.0.0.1*")
                .withSockJS();
        
        // Endpoint sin SockJS para clientes nativos
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("http://localhost*", "http://127.0.0.1*");
    }
}
