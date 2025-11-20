package com.cafeteriasoma.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cafeteriasoma.app.entity.Calificacion;
import com.cafeteriasoma.app.entity.Producto;
import com.cafeteriasoma.app.entity.Usuario;

@Repository
public interface CalificacionRepository extends JpaRepository<Calificacion, Long> {

    List<Calificacion> findByProducto(Producto producto);

    List<Calificacion> findByUsuario(Usuario usuario);

    Optional<Calificacion> findByUsuarioAndProducto(Usuario usuario, Producto producto);

    boolean existsByUsuarioAndProducto(Usuario usuario, Producto producto);
}
