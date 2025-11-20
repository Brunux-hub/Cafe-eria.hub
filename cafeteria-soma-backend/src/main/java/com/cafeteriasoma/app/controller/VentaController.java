package com.cafeteriasoma.app.controller;

import com.cafeteriasoma.app.controller.WebSocketController;
import com.cafeteriasoma.app.entity.Venta;
import com.cafeteriasoma.app.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para gestión de ventas.
 * Endpoint base: /api/ventas
 */
@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private WebSocketController webSocketController;

    /**
     * GET /api/ventas
     * Obtiene todas las ventas con paginación (requiere autenticación)
     */
    @GetMapping
    public ResponseEntity<?> getAllVentas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "fechaCreacion,desc") String[] sort) {
        
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fechaCreacion"));
            Page<Venta> ventasPage = ventaRepository.findAll(pageable);
            
            Map<String, Object> response = new HashMap<>();
            response.put("ventas", ventasPage.getContent());
            response.put("currentPage", ventasPage.getNumber());
            response.put("totalItems", ventasPage.getTotalElements());
            response.put("totalPages", ventasPage.getTotalPages());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/ventas/{id}
     * Obtiene una venta por ID con sus detalles
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getVentaById(@PathVariable Long id) {
        return ventaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/ventas/usuario/{usuarioId}
     * Obtiene las ventas de un usuario específico
     */
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Venta>> getVentasByUsuario(@PathVariable Long usuarioId) {
        List<Venta> ventas = ventaRepository.findByUsuarioIdUsuarioOrderByFechaVentaDesc(usuarioId);
        return ResponseEntity.ok(ventas);
    }

    /**
     * GET /api/ventas/fecha-rango
     * Obtiene ventas en un rango de fechas (solo ADMIN)
     */
    @GetMapping("/fecha-rango")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getVentasByFechaRango(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        
        List<Venta> ventas = ventaRepository.findByFechaVentaBetween(fechaInicio, fechaFin);
        
        // Calcular total de ventas en el rango
        BigDecimal totalVentas = ventas.stream()
                .map(Venta::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        Map<String, Object> response = new HashMap<>();
        response.put("ventas", ventas);
        response.put("cantidad", ventas.size());
        response.put("totalVentas", totalVentas);
        
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/ventas
     * Crea una nueva venta (requiere autenticación)
     */
    @PostMapping
    public ResponseEntity<?> createVenta(@RequestBody Venta venta) {
        try {
            // Validaciones básicas
            if (venta.getTotal() == null || venta.getTotal().compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "El total de la venta debe ser mayor a 0"));
            }
            
            if (venta.getUsuario() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Debe especificar un usuario para la venta"));
            }
            
            // Guardar venta
            Venta savedVenta = ventaRepository.save(venta);
            
            // Notificar nueva venta por WebSocket
            webSocketController.notifyNewSale(
                    savedVenta.getIdVenta(),
                    savedVenta.getUsuario().getCorreo(),
                    savedVenta.getTotal().doubleValue()
            );
            
            return ResponseEntity.status(HttpStatus.CREATED).body(savedVenta);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/ventas/estadisticas/hoy
     * Obtiene estadísticas de ventas del día actual (solo ADMIN)
     */
    @GetMapping("/estadisticas/hoy")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getEstadisticasHoy() {
        LocalDateTime inicioHoy = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime finHoy = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        
        List<Venta> ventasHoy = ventaRepository.findByFechaVentaBetween(inicioHoy, finHoy);
        
        BigDecimal totalVentas = ventasHoy.stream()
                .map(Venta::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        Map<String, Object> estadisticas = new HashMap<>();
        estadisticas.put("cantidadVentas", ventasHoy.size());
        estadisticas.put("totalVentas", totalVentas);
        estadisticas.put("promedioVenta", ventasHoy.isEmpty() ? BigDecimal.ZERO : 
                totalVentas.divide(BigDecimal.valueOf(ventasHoy.size()), 2, java.math.RoundingMode.HALF_UP));
        estadisticas.put("fecha", LocalDateTime.now());
        
        return ResponseEntity.ok(estadisticas);
    }

    /**
     * GET /api/ventas/estadisticas/mes
     * Obtiene estadísticas de ventas del mes actual (solo ADMIN)
     */
    @GetMapping("/estadisticas/mes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getEstadisticasMes() {
        LocalDateTime inicioMes = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime finMes = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        
        List<Venta> ventasMes = ventaRepository.findByFechaVentaBetween(inicioMes, finMes);
        
        BigDecimal totalVentas = ventasMes.stream()
                .map(Venta::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        Map<String, Object> estadisticas = new HashMap<>();
        estadisticas.put("cantidadVentas", ventasMes.size());
        estadisticas.put("totalVentas", totalVentas);
        estadisticas.put("promedioVenta", ventasMes.isEmpty() ? BigDecimal.ZERO : 
                totalVentas.divide(BigDecimal.valueOf(ventasMes.size()), 2, java.math.RoundingMode.HALF_UP));
        estadisticas.put("mes", LocalDateTime.now().getMonth());
        estadisticas.put("anio", LocalDateTime.now().getYear());
        
        return ResponseEntity.ok(estadisticas);
    }
}
