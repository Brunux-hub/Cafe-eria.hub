package com.cafeteriasoma.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cafeteriasoma.app.entity.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {}
