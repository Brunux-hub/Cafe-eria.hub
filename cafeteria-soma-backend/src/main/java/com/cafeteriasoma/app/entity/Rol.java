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
 * Tabla de roles del sistema (p.ej., ADMIN, CLIENTE).
 * Mantenerlo como entidad permite crecer (más roles/permisos) sin tocar el código.
 */
@Entity
@Table(
        name = "rol",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_rol_nombre", columnNames = "nombre")
        }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@ToString
@EqualsAndHashCode(of = "idRol")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Long idRol;

    @Column(name = "nombre", nullable = false, length = 30)
    private String nombre; // p.ej.: ADMIN, CLIENTE

    @Column(name = "descripcion", length = 150)
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
