package com.cafeteriasoma.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cafeteriasoma.app.entity.Categoria;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    
    Optional<Categoria> findByNombre(String nombre);
    
    List<Categoria> findByActivoTrue();
}