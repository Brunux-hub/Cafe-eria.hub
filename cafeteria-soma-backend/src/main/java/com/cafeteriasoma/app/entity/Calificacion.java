package com.cafeteriasoma.app.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
 * Representa una calificación o comentario dejado por un cliente sobre un producto.
 * Los usuarios deben estar autenticados para dejar una reseña.
 */
@Entity
@Table(
        name = "calificacion",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_usuario_producto",
                        columnNames = {"id_usuario", "id_producto"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"usuario", "producto"})
@EqualsAndHashCode(of = "idCalificacion")
public class Calificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_calificacion")
    private Long idCalificacion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "id_usuario",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_calificacion_usuario")
    )
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "id_producto",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_calificacion_producto")
    )
    private Producto producto;

    @Column(name = "puntuacion", nullable = false)
    private Integer puntuacion; // Rango: 1 a 5 estrellas

    @Column(name = "comentario", length = 500)
    private String comentario;

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;
}
