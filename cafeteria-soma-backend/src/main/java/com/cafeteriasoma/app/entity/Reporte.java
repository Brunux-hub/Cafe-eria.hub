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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Representa un reporte generado por un administrador (por ejemplo: ventas del mes, usuarios registrados, etc.)
 * Guarda la información básica para control de auditoría.
 */
@Entity
@Table(name = "reporte")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "usuarioAdmin")
@EqualsAndHashCode(of = "idReporte")
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reporte")
    private Long idReporte;

    @Column(name = "titulo", nullable = false, length = 150)
    private String titulo;

    @Column(name = "descripcion", length = 300)
    private String descripcion;

    @Column(name = "tipo", nullable = false, length = 50)
    private String tipo; // Ejemplo: "VENTAS", "USUARIOS", "PRODUCTOS"

    @Column(name = "ruta_pdf", nullable = false, length = 255)
    private String rutaPdf; // Ruta del archivo generado en el sistema

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "id_usuario_admin",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_reporte_usuario_admin")
    )
    private Usuario usuarioAdmin;

    @CreationTimestamp
    @Column(name = "fecha_generacion", updatable = false)
    private LocalDateTime fechaGeneracion;
}
