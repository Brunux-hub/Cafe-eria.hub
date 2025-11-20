package com.cafeteriasoma.app.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
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
 * Usuarios del sistema (administradores y clientes).
 * NOTA: La contraseña se almacena hasheada (BCrypt). No expongas esta entidad en las respuestas HTTP:
 * usa DTOs de respuesta y oculta 'contrasena'.
 */
@Entity
@Table(
        name = "usuario",
        indexes = {
                @Index(name = "idx_usuario_correo", columnList = "correo")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_usuario_correo", columnNames = "correo")
        }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@ToString(exclude = "contrasena")
@EqualsAndHashCode(of = "idUsuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "correo", nullable = false, length = 120)
    private String correo; // se normaliza a minúsculas en @PrePersist/@PreUpdate

    @JsonIgnore
    @Column(name = "contrasena", nullable = false, length = 255)
    private String contrasena; // hash (BCrypt)

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "id_rol",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_usuario_rol")
    )
    private Rol rol;

    @Column(name = "activo", nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    /** Normaliza el correo a minúsculas para consistencia y unicidad real. */
    @PrePersist
    @PreUpdate
    private void normalize() {
        if (correo != null) correo = correo.trim().toLowerCase();
    }
}
