package com.cafeteriasoma.app.controller;

import com.cafeteriasoma.app.entity.Promocion;
import com.cafeteriasoma.app.repository.PromocionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador REST para gestión de promociones.
 * Endpoint base: /api/promociones
 */
@RestController
@RequestMapping("/api/promociones")
public class PromocionController {

    @Autowired
    private PromocionRepository promocionRepository;

    /**
     * GET /api/promociones
     * Obtiene todas las promociones (requiere autenticación)
     */
    @GetMapping
    public ResponseEntity<List<Promocion>> getAllPromociones() {
        List<Promocion> promociones = promocionRepository.findAll();
        return ResponseEntity.ok(promociones);
    }

    /**
     * GET /api/promociones/activas
     * Obtiene promociones activas y vigentes (público)
     */
    @GetMapping("/activas")
    public ResponseEntity<List<Promocion>> getPromocionesActivas() {
        LocalDateTime now = LocalDateTime.now();
        List<Promocion> promociones = promocionRepository.findByActivoTrueAndFechaInicioBeforeAndFechaFinAfter(now, now);
        return ResponseEntity.ok(promociones);
    }

    /**
     * GET /api/promociones/{id}
     * Obtiene una promoción por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getPromocionById(@PathVariable Long id) {
        return promocionRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/promociones
     * Crea una nueva promoción (solo ADMIN)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createPromocion(@RequestBody Promocion promocion) {
        try {
            if (promocion.getActivo() == null) {
                promocion.setActivo(true);
            }
            
            // Validar fechas
            if (promocion.getFechaInicio() != null && promocion.getFechaFin() != null) {
                if (promocion.getFechaInicio().isAfter(promocion.getFechaFin())) {
                    return ResponseEntity.badRequest()
                            .body(java.util.Map.of("error", "La fecha de inicio no puede ser posterior a la fecha de fin"));
                }
            }
            
            Promocion savedPromocion = promocionRepository.save(promocion);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPromocion);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(java.util.Map.of("error", e.getMessage()));
        }
    }

    /**
     * PUT /api/promociones/{id}
     * Actualiza una promoción existente (solo ADMIN)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updatePromocion(@PathVariable Long id, @RequestBody Promocion promocion) {
        return promocionRepository.findById(id)
                .map(existing -> {
                    existing.setNombre(promocion.getNombre());
                    existing.setDescripcion(promocion.getDescripcion());
                    existing.setPorcentajeDescuento(promocion.getPorcentajeDescuento());
                    existing.setFechaInicio(promocion.getFechaInicio());
                    existing.setFechaFin(promocion.getFechaFin());
                    existing.setActivo(promocion.getActivo());
                    
                    Promocion updated = promocionRepository.save(existing);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * DELETE /api/promociones/{id}
     * Desactiva una promoción (soft delete) (solo ADMIN)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deletePromocion(@PathVariable Long id) {
        return promocionRepository.findById(id)
                .map(promocion -> {
                    promocion.setActivo(false);
                    promocionRepository.save(promocion);
                    return ResponseEntity.ok(java.util.Map.of("message", "Promoción desactivada exitosamente"));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/promociones/{id}/productos/{productoId}
     * Asocia un producto a una promoción (solo ADMIN)
     */
    @PostMapping("/{id}/productos/{productoId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addProductoToPromocion(
            @PathVariable Long id, 
            @PathVariable Long productoId) {
        // Esta funcionalidad requiere acceso al ProductoRepository
        // La implementaremos cuando actualicemos las relaciones
        return ResponseEntity.ok(java.util.Map.of("message", "Funcionalidad en desarrollo"));
    }
}
