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
 * Representa la relación entre Producto y Promoción (N:N),
 * permitiendo añadir campos adicionales en el futuro (fecha de asignación, etc.).
 */
@Entity
@Table(
        name = "producto_promocion",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_producto_promocion", columnNames = {"id_producto", "id_promocion"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"producto", "promocion"})
@EqualsAndHashCode(of = "id")
public class ProductoPromocion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "id_producto",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_producto_promocion_producto")
    )
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "id_promocion",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_producto_promocion_promocion")
    )
    private Promocion promocion;

    @CreationTimestamp
    @Column(name = "fecha_asignacion", updatable = false)
    private LocalDateTime fechaAsignacion;
}
