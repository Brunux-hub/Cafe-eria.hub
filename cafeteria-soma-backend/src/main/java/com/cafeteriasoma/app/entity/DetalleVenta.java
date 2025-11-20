package com.cafeteriasoma.app.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Detalle de productos asociados a una venta.
 * Cada registro representa un producto vendido con su cantidad y subtotal.
 */
@Entity
@Table(name = "detalle_venta")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"venta", "producto"})
@EqualsAndHashCode(of = "idDetalle")
public class DetalleVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private Long idDetalle;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "id_venta",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_detalle_venta_venta")
    )
    private Venta venta;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "id_producto",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_detalle_venta_producto")
    )
    private Producto producto;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;
}
