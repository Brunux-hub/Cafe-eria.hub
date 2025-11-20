package com.cafeteriasoma.app.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Agrupa productos (Café, Postres, Bebidas, etc.).
 * Mantenemos la relación con Producto como @ManyToOne desde Producto
 * para evitar cargar colecciones innecesarias aquí (mejor performance).
 */
@Entity
@Table(
        name = "categoria",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_categoria_nombre", columnNames = "nombre")
        }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@ToString
@EqualsAndHashCode(of = "idCategoria")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Long idCategoria;

    @Column(name = "nombre", nullable = false, length = 60)
    private String nombre;

    @Column(name = "descripcion", length = 200)
    private String descripcion;

    @Column(name = "activo", nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
}
