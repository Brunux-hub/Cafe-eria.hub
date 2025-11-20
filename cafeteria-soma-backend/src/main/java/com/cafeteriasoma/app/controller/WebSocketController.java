package com.cafeteriasoma.app.controller;

import com.cafeteriasoma.app.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador WebSocket para comunicación en tiempo real.
 * Maneja eventos como login/logout, nuevas ventas, actualizaciones de inventario.
 */
@Controller
public class WebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private SessionService sessionService;

    /**
     * Notifica a todos los usuarios cuando alguien se conecta
     */
    @MessageMapping("/user.connect")
    @SendTo("/topic/users")
    public Map<String, Object> userConnect(Map<String, String> payload) {
        String username = payload.get("username");
        
        Map<String, Object> response = new HashMap<>();
        response.put("type", "USER_CONNECTED");
        response.put("username", username);
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("activeUsers", sessionService.countActiveUsers());
        
        return response;
    }

    /**
     * Notifica a todos los usuarios cuando alguien se desconecta
     */
    @MessageMapping("/user.disconnect")
    @SendTo("/topic/users")
    public Map<String, Object> userDisconnect(Map<String, String> payload) {
        String username = payload.get("username");
        
        Map<String, Object> response = new HashMap<>();
        response.put("type", "USER_DISCONNECTED");
        response.put("username", username);
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("activeUsers", sessionService.countActiveUsers());
        
        return response;
    }

    /**
     * Notifica nuevas ventas en tiempo real
     */
    public void notifyNewSale(Long saleId, String username, Double total) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "NEW_SALE");
        notification.put("saleId", saleId);
        notification.put("username", username);
        notification.put("total", total);
        notification.put("timestamp", LocalDateTime.now().toString());
        
        messagingTemplate.convertAndSend("/topic/sales", notification);
    }

    /**
     * Notifica cambios en el inventario
     */
    public void notifyInventoryChange(Long productId, String productName, Integer newStock) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "INVENTORY_UPDATE");
        notification.put("productId", productId);
        notification.put("productName", productName);
        notification.put("newStock", newStock);
        notification.put("timestamp", LocalDateTime.now().toString());
        
        messagingTemplate.convertAndSend("/topic/inventory", notification);
    }

    /**
     * Envía notificación a un usuario específico
     */
    public void sendToUser(String username, String destination, Object payload) {
        messagingTemplate.convertAndSendToUser(username, destination, payload);
    }
}
