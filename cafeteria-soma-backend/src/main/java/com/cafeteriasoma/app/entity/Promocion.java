package com.cafeteriasoma.app.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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
 * Representa una promoción o descuento aplicable a uno o varios productos.
 * Por ejemplo: "Semana del Café", "2x1 en postres", "Descuento del 10%".
 */
@Entity
@Table(
        name = "promocion",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_promocion_nombre", columnNames = "nombre")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "productos")
@EqualsAndHashCode(of = "idPromocion")
public class Promocion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_promocion")
    private Long idPromocion;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @Column(name = "porcentaje_descuento", nullable = false, precision = 5, scale = 2)
    private BigDecimal porcentajeDescuento;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Column(name = "activo", nullable = false)
    @Builder.Default
    private Boolean activo = true;

    /** Relación bidireccional con Producto (N:N). */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "producto_promocion",
            joinColumns = @JoinColumn(name = "id_promocion", foreignKey = @ForeignKey(name = "fk_producto_promocion_promocion")),
            inverseJoinColumns = @JoinColumn(name = "id_producto", foreignKey = @ForeignKey(name = "fk_producto_promocion_producto"))
    )
    @Builder.Default
    private Set<Producto> productos = new HashSet<>();

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
}
