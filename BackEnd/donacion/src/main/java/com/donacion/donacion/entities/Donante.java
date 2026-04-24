package com.donacion.donacion.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "donantes")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "tipo_donante", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@NoArgsConstructor
public abstract class Donante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rut", nullable = false, unique = true, length = 12)
    private String rut;

    @Column(name = "email", nullable = false, length = 150)
    private String email;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "direccion", length = 255)
    private String direccion;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;
    

    /**
     * Relación inversa: un donante puede tener múltiples recursos donados
     * a lo largo del tiempo (historial de donaciones).
     */
    @OneToMany(mappedBy = "donante", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RecursoDonado> recursos = new ArrayList<>();

    @PrePersist
    protected void onPersist() {
        this.fechaRegistro = LocalDateTime.now();
    }

    /**
     * Devuelve el nombre visible del donante (implementado por cada subclase).
     */
    public abstract String getNombreVisible();
}