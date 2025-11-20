package com.cafeteriasoma.app.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cafeteriasoma.app.entity.Usuario;
import com.cafeteriasoma.app.entity.Venta;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {

    List<Venta> findByUsuario(Usuario usuario);
    
    List<Venta> findByUsuarioIdUsuarioOrderByFechaVentaDesc(Long usuarioId);
    
    List<Venta> findByFechaVentaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    List<Venta> findTop10ByOrderByFechaVentaDesc();
}
