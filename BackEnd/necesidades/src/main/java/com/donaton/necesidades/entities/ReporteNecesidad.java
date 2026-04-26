package com.donaton.necesidades.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.OneToMany;

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
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


//Necesidad Concreta en el terreno dentro de la ZonaAfectada
@Entity
@Table(name = "reporte_necesidad")
@Getter
@Setter
@NoArgsConstructor
public class ReporteNecesidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zona_afectada_id", nullable = false)
    private ZonaAfectada zonaAfectada;

    @Column(name = "titulo", nullable = false, length = 200)
    private String titulo;

    @Column(name = "descripcion", nullable = false, length = 1000)
    private String descripcion;


    //Tipo de recurso necesario (Usamos el mismo enum que el micro de Donacion y Logistica por coherencia semántica)

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_recurso_necesario", nullable = false, length = 30)
    private TipoRecurso tipoRecursoNecesario;

    //Cantidad requerida del recurso (500 kg de alimentos por ejemplo)
    @Column(name = "cantidad_requerida", nullable = false, precision = 12, scale = 2)
    private BigDecimal cantidadRequerida;

    @Column(name = "unidad_medida", nullable = false, length = 30)
    private String unidadMedida;


    //Cantidad ya atentida por asignación de recursos

    @Column(name = "cantidad_atendida", nullable = false, precision = 12, scale = 2)
    private BigDecimal cantidadAtendida = BigDecimal.ZERO;


    //Nivel de urgencia para priorizar en el panel de administrador

    @Enumerated(EnumType.STRING)
    @Column(name = "prioridad", nullable = false, length = 10)
    private Prioridad prioridad = Prioridad.MEDIA;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 30)
    private EstadoReporte estado = EstadoReporte.PENDIENTE;


    //Nombre del voluntario o funcionario que reportó la necesidad

    @Column(name = "reportado_por", nullable = false, length =  150)
    private String reportadoPor;

    @Column(name = "telefono_contacto", length = 20)
    private String telefonoContacto;

    @Column(name ="obervaciones", length = 20)
    private String observaciones;

    @Column(name = "fecha_reporte", nullable = false, updatable = false)
    private LocalDateTime fechaReporte;

    @Column(name = "fecha_ultima_actualizacion")
    private LocalDateTime fechaUltimaActualizacion;

    //Asignacion de recursos del microservicio Logística
    @OneToMany(mappedBy = "reporteNecesidad", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AsignacionRecurso> asignaciones = new ArrayList<>();

    @PrePersist
    protected void onPersist(){
        this.fechaReporte = LocalDateTime.now();
        this.fechaUltimaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate(){
        this.fechaUltimaActualizacion = LocalDateTime.now();
    }

    //Registra una cantidad atendida y recalcula el estado de reporte
    public void registrarAtencion(BigDecimal cantidadAsignada){
        this.cantidadAtendida = this.cantidadAtendida.add(cantidadAsignada);
        recalcularEstado();
    }

    private void recalcularEstado(){
        int comparacion = this.cantidadAtendida.compareTo(this.cantidadRequerida);
        if (comparacion >= 0){
            this.estado = EstadoReporte.ATENDIDA;
        } else if(this.cantidadAtendida.compareTo(BigDecimal.ZERO) >0){
            this.estado = EstadoReporte.PARCIALMENTE_ATENDIDA;
        }
    }


    //Porcentaje de avance de atencion (0 a 100%)
    public int getPorcentajeAtencion(){
        if(cantidadRequerida.compareTo(BigDecimal.ZERO) == 0) return 100;
        return cantidadAtendida
            .multiply(BigDecimal.valueOf(100))
            .divide(cantidadRequerida, 0, java.math.RoundingMode.DOWN)
            .min(BigDecimal.valueOf(100))
            .intValue();
    }

    public enum TipoRecurso {
        DINERO,
        ALIMENTO,
        ROPA,
        INSUMOS_MEDICOS,
        INSUMOS_HIGIENE,
        OTRO
    }

    public enum Prioridad {
        CRITICA,
        ALTA,
        MEDIA,
        BAJA
    }

    public enum EstadoReporte {
        PENDIENTE,
        EN_GESTION,
        PARCIALMENTE_ATENDIDA,
        ATENDIDA,
        CANCELADA
    }

}
