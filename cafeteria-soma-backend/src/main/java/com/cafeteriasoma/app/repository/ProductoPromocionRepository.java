package com.cafeteriasoma.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cafeteriasoma.app.entity.Producto;
import com.cafeteriasoma.app.entity.ProductoPromocion;
import com.cafeteriasoma.app.entity.Promocion;

@Repository
public interface ProductoPromocionRepository extends JpaRepository<ProductoPromocion, Long> {

    List<ProductoPromocion> findByProducto(Producto producto);

    List<ProductoPromocion> findByPromocion(Promocion promocion);

    boolean existsByProductoAndPromocion(Producto producto, Promocion promocion);
}
