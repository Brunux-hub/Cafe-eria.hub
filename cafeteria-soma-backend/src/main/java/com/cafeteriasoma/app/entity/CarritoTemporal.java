package com.cafeteriasoma.app.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Representa los productos añadidos al carrito por un usuario antes de concretar la compra.
 * Ideal para almacenar el estado temporal del proceso de compra.
 */
@Entity
@Table(
        name = "carrito_temporal",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_carrito_usuario_producto",
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
@EqualsAndHashCode(of = "idCarrito")
public class CarritoTemporal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_carrito")
    private Long idCarrito;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "id_usuario",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_carrito_usuario")
    )
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "id_producto",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_carrito_producto")
    )
    private Producto producto;

    @Column(name = "cantidad", nullable = false)
    @Builder.Default
    private Integer cantidad = 1;

    @CreationTimestamp
    @Column(name = "fecha_agregado", updatable = false)
    private LocalDateTime fechaAgregado;
}
