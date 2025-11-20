package com.cafeteriasoma.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cafeteriasoma.app.entity.CarritoTemporal;
import com.cafeteriasoma.app.entity.Producto;
import com.cafeteriasoma.app.entity.Usuario;

@Repository
public interface CarritoTemporalRepository extends JpaRepository<CarritoTemporal, Long> {

    List<CarritoTemporal> findByUsuario(Usuario usuario);

    Optional<CarritoTemporal> findByUsuarioAndProducto(Usuario usuario, Producto producto);

    void deleteByUsuario(Usuario usuario);
}
