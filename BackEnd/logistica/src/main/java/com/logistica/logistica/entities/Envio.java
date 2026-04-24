package com.logistica.logistica.entities;

import java.time.LocalDate;
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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


//Acá representaremos un envío de recursos desde un Centro de Acopio
//Hacia un destino final (damnificados, municipalidad, etc).
//Planificado -> En_Transito -> Entregado -> Cancelado
@Entity
@Table(name = "envios")
@Getter
@Setter
@NoArgsConstructor
public class Envio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Número de seguimiento visible
    @Column(name = "numero_seguimiento", nullable = false, unique = true, length = 20)
    private String numeroSeguimiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "centro_origen_id", nullable = false )
    private CentroAcopio centroOrigen;



    //Descripcion del destino (puede ser municipalidad, albergue, etc)
    @Column(name = "destino_descripcion", nullable = false, length = 300)
    private String destinoDescripcion;

    @Column (name = "destino_direccion", nullable = false, length = 255)
    private String destinoDireccion;

    @Column(name = "destino_region", nullable = false, length = 100 )
    private String destinoRegion;

    @Column(name = "destino_comuna", nullable = false, length = 100)
    private String destinoComuna;



    //Datos del transportista asignado
    @Column(name = "transportista_nombre", length = 150)
    private String transportistaNombre;

    @Column(name = "transportista_rut", length = 13)
    private String transportistaRut;

    @Column(name = "patente_vehiculo", length = 10)
    private String patenteVehiculo;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoEnvio estado = EstadoEnvio.PLANIFICADO;

    @Column(name = "fecha_planificada")
    private LocalDate fechaPlanificada;

    @Column(name = "fecha_despacho")
    private LocalDateTime fechaDespacho;

    @Column(name = "fecha_entrega")
    private LocalDateTime fechaEntrega;

    @Column(name = "observaciones", length = 500)
    private String observaciones;

    @Column(name = "fecha_creacion", nullable = false , updatable = false)
    private LocalDateTime fechaCreacion;

    

    //Detalle de los recursos incluidos en este envio
    @OneToMany(mappedBy = "envio", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DetalleEnvio> detalle = new ArrayList<>();

    @PrePersist
    protected void onPersist() {
        this.fechaCreacion = LocalDateTime.now();
    }

    //Marcar el envío como despachado
    public void despachar(){
        if (this.estado != EstadoEnvio.PLANIFICADO){
            throw new IllegalStateException("Solo se pueden despachar envíos en estado PLANIFICADO.");
        }
        this.estado = EstadoEnvio.EN_TRANSITO;
        this.fechaDespacho = LocalDateTime.now();
       
    }


    //Marcar el envío como entregado
    public void confirmarEntrega (String observaciones){
        if (this.estado != EstadoEnvio.EN_TRANSITO){
            throw new IllegalStateException("Solo se puede confirmar envíos EN_TRANSITO.");
        }
        this.estado = EstadoEnvio.ENTREGADO;
        this.fechaEntrega = LocalDateTime.now();
        this.observaciones = observaciones;
    }



    //Cancelar el envío si aún no fue despachado
    public void cancelar (String motivo){
        if(this.estado == EstadoEnvio.ENTREGADO){
            throw new IllegalStateException("No se puede cancelar el envío ya entregado.");
        }
        this.estado = EstadoEnvio.CANCELADO;
        this.observaciones = motivo;
    }


    public enum EstadoEnvio {
        PLANIFICADO,
        EN_TRANSITO,
        ENTREGADO,
        CANCELADO
    }

}
