package com.cafeteriasoma.app.controller;

import com.cafeteriasoma.app.controller.WebSocketController;
import com.cafeteriasoma.app.dto.producto.CreateProductoRequest;
import com.cafeteriasoma.app.dto.producto.ProductoDto;
import com.cafeteriasoma.app.dto.producto.UpdateProductoRequest;
import com.cafeteriasoma.app.entity.Producto;
import com.cafeteriasoma.app.repository.ProductoRepository;
import com.cafeteriasoma.app.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador REST para gestión de productos.
 * Endpoint base: /api/productos
 * CORS configurado a nivel global en SecurityConfig.
 */
@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private WebSocketController webSocketController;

    /**
     * GET /api/productos
     * Obtiene todos los productos (público)
     */
    @GetMapping
    public ResponseEntity<List<ProductoDto>> getAllProductos() {
        List<ProductoDto> productos = productoService.getAllProductos();
        return ResponseEntity.ok(productos);
    }

    /**
     * GET /api/productos/disponibles
     * Obtiene productos disponibles (stock > 0) (público)
     */
    @GetMapping("/disponibles")
    public ResponseEntity<?> getProductosDisponibles() {
        List<Producto> productos = productoRepository.findByStockGreaterThanAndActivoTrue(0);
        return ResponseEntity.ok(productos);
    }

    /**
     * GET /api/productos/categoria/{categoriaId}
     * Obtiene productos por categoría (público)
     */
    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<?> getProductosByCategoria(@PathVariable Long categoriaId) {
        List<Producto> productos = productoRepository.findByCategoriaIdCategoriaAndActivoTrue(categoriaId);
        return ResponseEntity.ok(productos);
    }

    /**
     * GET /api/productos/{id}
     * Obtiene un producto por ID (público)
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductoDto> getProductoById(@PathVariable Long id) {
        ProductoDto producto = productoService.getProductoById(id);
        return ResponseEntity.ok(producto);
    }

    /**
     * POST /api/productos
     * Crea un nuevo producto (solo ADMIN)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductoDto> createProducto(@Valid @RequestBody CreateProductoRequest request) {
        ProductoDto producto = productoService.createProducto(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(producto);
    }

    /**
     * PUT /api/productos/{id}
     * Actualiza un producto existente (solo ADMIN)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductoDto> updateProducto(@PathVariable Long id, @Valid @RequestBody UpdateProductoRequest request) {
        ProductoDto producto = productoService.updateProducto(id, request);
        return ResponseEntity.ok(producto);
    }

    /**
     * PATCH /api/productos/{id}/stock
     * Actualiza el stock de un producto (solo ADMIN)
     */
    @PatchMapping("/{id}/stock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateStock(@PathVariable Long id, @RequestBody Map<String, Integer> request) {
        Integer nuevoStock = request.get("stock");
        
        if (nuevoStock == null || nuevoStock < 0) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "El stock debe ser un número positivo"));
        }
        
        return productoRepository.findById(id)
                .map(producto -> {
                    producto.setStock(nuevoStock);
                    Producto updated = productoRepository.save(producto);
                    
                    // Notificar cambio de inventario por WebSocket
                    webSocketController.notifyInventoryChange(
                            updated.getIdProducto(),
                            updated.getNombre(),
                            updated.getStock()
                    );
                    
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * DELETE /api/productos/{id}
     * Desactiva un producto (soft delete) (solo ADMIN)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProducto(@PathVariable Long id) {
        productoService.deleteProducto(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * PATCH /api/productos/{id}/activar
     * Activa un producto desactivado (solo ADMIN)
     */
    @PatchMapping("/{id}/activar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> activarProducto(@PathVariable Long id) {
        return productoRepository.findById(id)
                .map(producto -> {
                    producto.setActivo(true);
                    productoRepository.save(producto);
                    return ResponseEntity.ok(Map.of("message", "Producto activado exitosamente"));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
