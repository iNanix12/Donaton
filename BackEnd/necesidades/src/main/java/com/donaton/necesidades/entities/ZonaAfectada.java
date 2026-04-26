package com.donaton.necesidades.entities;

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

//Representa una zona geográfica afectada por una emergencia o desastre

@Entity
@Table(name="zonas_afectadas")
@Getter
@Setter
@NoArgsConstructor
public class ZonaAfectada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 200)
    private String nombre;

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @Column(name = "region", nullable = false, length = 100)
    private String region;

    @Column(name = "comuna", nullable = false, length = 100)
    private String comuna;

    @Column(name = "direccion_referencia", length = 300)
    private String direccionReferencia;

    //Coordenadas para visualización en mapa en el frontEnd

    @Column(name = "latitud")
    private Double latitud;

    @Column(name = "longitud")
    private Double longitud;

    //Tipo de emergencia que originó la zona afectada
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_emergencia", nullable = false, length = 30)
    private TipoEmergencia tipoEmergencia;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_emergencia", nullable = false, length = 30)
    private EstadoZona estado = EstadoZona.ACTIVA;


    //Número estimado de personas afectadas en la zona
    @Column(name = "personas_afectadas")
    private Integer personasAfectadas;

    @Column(name = "fecha_apertura", nullable = false, updatable = false)
    private LocalDateTime fechaApertura;

    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;

    //Nombre del coordinador de terreno responsable de la zona

    @Column (name = "coordinador_nombre", length = 150)
    private String coordinadorNombre;

    @Column(name = "coordinador_telefono", length = 20)
    private String coordinadorTelefono;

    //Reportes de necesidades registrados dentro de esta zona

    @OneToMany(mappedBy = "zonaAfectada", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ReporteNecesidad> reportes = new ArrayList<>();

    @PrePersist
    protected void onPersist(){
        this.fechaApertura = LocalDateTime.now();
    }

    //Cierra la zona cuando la emergencia ha sido resuelta
    public void cerrar() {
        this.estado = EstadoZona.CERRADA;
        this.fechaCierre = LocalDateTime.now();
    }


    public enum TipoEmergencia {
        TERREMOTO,
        INCENDIO,
        INUNDACION,
        ALUVION,
        ERUPCION_VOLCANICA,
        SEQUIA,
        ACCIDENTE_MASIVO,
        OTRO
    }

    public enum EstadoZona {
        ACTIVA, 
        EN_SEGUIMIENTO,
        CERRADA
    }




    



}
