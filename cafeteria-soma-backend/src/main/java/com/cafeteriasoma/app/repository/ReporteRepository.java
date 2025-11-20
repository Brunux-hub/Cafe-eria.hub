package com.cafeteriasoma.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cafeteriasoma.app.entity.Reporte;
import com.cafeteriasoma.app.entity.Usuario;

@Repository
public interface ReporteRepository extends JpaRepository<Reporte, Long> {

    List<Reporte> findByUsuarioAdmin(Usuario usuarioAdmin);

    List<Reporte> findByTipo(String tipo);
}
