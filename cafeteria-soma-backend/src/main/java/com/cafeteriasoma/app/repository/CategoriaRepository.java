package com.cafeteriasoma.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cafeteriasoma.app.entity.Categoria;

import java.util.Optional;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    Optional<Categoria> findByNombre(String nombre);
}