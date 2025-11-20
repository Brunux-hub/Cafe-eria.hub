package com.cafeteriasoma.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cafeteriasoma.app.entity.Producto;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    
    // Buscar por nombre
    Optional<Producto> findByNombre(String nombre);
    
    // Buscar productos activos
    List<Producto> findByActivoTrue();
    
    // Buscar productos con stock disponible
    List<Producto> findByStockGreaterThanAndActivoTrue(Integer minStock);
    
    // Buscar productos por categoría
    List<Producto> findByCategoriaIdCategoriaAndActivoTrue(Long categoriaId);
    
    // Buscar productos por rango de precio
    List<Producto> findByPrecioBetween(Double precioMin, Double precioMax);
}
