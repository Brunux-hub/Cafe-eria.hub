package com.cafeteriasoma.app.controller;

import com.cafeteriasoma.app.entity.Categoria;
import com.cafeteriasoma.app.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestión de categorías de productos.
 * Endpoint base: /api/categorias
 */
@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaRepository categoriaRepository;

    /**
     * GET /api/categorias
     * Obtiene todas las categorías (público)
     */
    @GetMapping
    public ResponseEntity<List<Categoria>> getAllCategorias() {
        List<Categoria> categorias = categoriaRepository.findAll();
        return ResponseEntity.ok(categorias);
    }

    /**
     * GET /api/categorias/activas
     * Obtiene solo las categorías activas (público)
     */
    @GetMapping("/activas")
    public ResponseEntity<List<Categoria>> getCategoriasActivas() {
        List<Categoria> categorias = categoriaRepository.findByActivoTrue();
        return ResponseEntity.ok(categorias);
    }

    /**
     * GET /api/categorias/{id}
     * Obtiene una categoría por ID (público)
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoriaById(@PathVariable Long id) {
        return categoriaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/categorias
     * Crea una nueva categoría (solo ADMIN)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createCategoria(@RequestBody Categoria categoria) {
        try {
            if (categoria.getActivo() == null) {
                categoria.setActivo(true);
            }
            Categoria savedCategoria = categoriaRepository.save(categoria);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedCategoria);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(java.util.Map.of("error", e.getMessage()));
        }
    }

    /**
     * PUT /api/categorias/{id}
     * Actualiza una categoría existente (solo ADMIN)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateCategoria(@PathVariable Long id, @RequestBody Categoria categoria) {
        return categoriaRepository.findById(id)
                .map(existing -> {
                    existing.setNombre(categoria.getNombre());
                    existing.setDescripcion(categoria.getDescripcion());
                    existing.setActivo(categoria.getActivo());
                    Categoria updated = categoriaRepository.save(existing);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * DELETE /api/categorias/{id}
     * Desactiva una categoría (soft delete) (solo ADMIN)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteCategoria(@PathVariable Long id) {
        return categoriaRepository.findById(id)
                .map(categoria -> {
                    categoria.setActivo(false);
                    categoriaRepository.save(categoria);
                    return ResponseEntity.ok(java.util.Map.of("message", "Categoría desactivada exitosamente"));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
