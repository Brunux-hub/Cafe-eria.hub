package com.cafeteriasoma.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cafeteriasoma.app.entity.Promocion;

@Repository
public interface PromocionRepository extends JpaRepository<Promocion, Long> {
    Optional<Promocion> findByNombre(String nombre);
}
