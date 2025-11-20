package com.cafeteriasoma.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cafeteriasoma.app.entity.DetalleVenta;
import com.cafeteriasoma.app.entity.Venta;

@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {

    List<DetalleVenta> findByVenta(Venta venta);
}
