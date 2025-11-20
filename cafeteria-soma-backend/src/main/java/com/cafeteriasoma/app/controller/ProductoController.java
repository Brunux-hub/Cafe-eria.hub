package com.cafeteriasoma.app.controller;

import com.cafeteriasoma.app.dto.producto.CreateProductoRequest;
import com.cafeteriasoma.app.dto.producto.ProductoDto;
import com.cafeteriasoma.app.dto.producto.UpdateProductoRequest;
import com.cafeteriasoma.app.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @GetMapping
    public ResponseEntity<List<ProductoDto>> getAllProductos() {
        List<ProductoDto> productos = productoService.getAllProductos();
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoDto> getProductoById(@PathVariable Long id) {
        ProductoDto producto = productoService.getProductoById(id);
        return ResponseEntity.ok(producto);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductoDto> createProducto(@Valid @RequestBody CreateProductoRequest request) {
        ProductoDto producto = productoService.createProducto(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(producto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductoDto> updateProducto(@PathVariable Long id, @Valid @RequestBody UpdateProductoRequest request) {
        ProductoDto producto = productoService.updateProducto(id, request);
        return ResponseEntity.ok(producto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProducto(@PathVariable Long id) {
        productoService.deleteProducto(id);
        return ResponseEntity.noContent().build();
    }
}
