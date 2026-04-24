package com.logistica.logistica.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *  Representa un centro de acopio físico donde se almacenan
 * temporalmente los recursos donados antes de su distribución.
 * Un centro tiene un inventario de recursos y puede ser origen de múltiples envíos.
 */

@Entity
@Table(name ="centros_acopio")
@Getter
@Setter
@NoArgsConstructor
public class CentroAcopio {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @Column(name = "direccion", nullable = false, length = 255)
    private String direccion;

    @Column(name = "region", nullable = true, length = 100)
    private String region;

    @Column(name = "comuna", nullable = true, length = 100)
    private String comuna;

    // Coordenadas para integración con maás en el frontEnd
    @Column(name = "latitud", precision = 10)
    private Double latitud;

    @Column(name = "longitud", precision = 10)
    private Double longitud;
    
    @Column(name = "telefono", length = 20)
    private String telefono;
    
    @Column(name = "email_encargado", length = 100)
    private String emailEncargado;

    @Column(name = "nombre_encargado", length = 150)
    private String nombreEncargado;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoCentro estado = EstadoCentro.ACTIVO;

    @Column(name = "capacidad_maxima_kg")
    private Integer capacidadMaximaKg;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;


    //Inventario actual del centro (en kg, litros, unidades, etc.)
    @OneToMany(mappedBy = "centroAcopio", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ItemInventario> inventario = new ArrayList<>();

    //Envíos que parten desde centro
    @OneToMany(mappedBy = "centroOrigen", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Envio> enviosOrigen = new ArrayList<>();

    @PrePersist
    protected void onPersist() {
        this.fechaCreacion = LocalDateTime.now();
    }

    public enum EstadoCentro {
        ACTIVO,
        INACTIVO,
        SATURADO
    }



}
