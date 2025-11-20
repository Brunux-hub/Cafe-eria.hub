package com.cafeteriasoma.app.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Ítems del catálogo. Usa BigDecimal para precios (evita errores de redondeo).
 */
@Entity
@Table(
        name = "producto",
        indexes = {
                @Index(name = "idx_producto_nombre", columnList = "nombre")
        }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@ToString(exclude = "categoria")
@EqualsAndHashCode(of = "idProducto")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Long idProducto;

    @Column(name = "nombre", nullable = false, length = 120)
    private String nombre;

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @Column(name = "precio", nullable = false, precision = 10, scale = 2)
    private BigDecimal precio; // p.ej. 12.50

    @Column(name = "stock", nullable = false)
    @Builder.Default
    private Integer stock = 0;

    @Column(name = "imagen_url", length = 512)
    private String imagenUrl;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "id_categoria",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_producto_categoria")
    )
    private Categoria categoria;

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
