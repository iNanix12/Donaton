package com.donaton.necesidades.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "asignaciones_Recurso")
@Getter
@Setter
@NoArgsConstructor
public class AsignacionRecurso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporte_necesidad_id", nullable = false)
    private ReporteNecesidad reporteNecesidad;

    @Column(name= "envio_id_logistica")
    private Long envioIdLogistica;

    //Numero de seguimiento del envío. GUardado localmente para trazabilidad sin depender de Logística

    @Column(name = "numero_Seguimiento_envio", length = 20)
    private String numeroSeguimientoEnvio;

    @Column(name = "cantidad_asignada", nullable = false, length = 2)
    private BigDecimal cantidadAsignada;

    @Column(name = "unidad_medida", nullable = false, length = 30)
    private String unidadMedida;

    @Enumerated(EnumType.STRING)
    @Column(name= "estado", nullable = false, length = 20)
    private EstadoAsignacion estado = EstadoAsignacion.PLANIFICADA;

    //Nombre del funcionario que realizó la asignación
    @Column(name = "asignado_por", nullable = false, length = 150)
    private String asignadoPor;

    @Column(name = "observaciones", length = 500)
    private String observaciones;

    @Column(name = "fecha_asignacion", nullable = false, updatable = false)
    private LocalDateTime fechaAsignacion;

    @Column(name = "fecha_confirmacion")
    private LocalDateTime fechaConfirmacion;

    @PrePersist
    protected void onPersist(){
        this.fechaAsignacion = LocalDateTime.now();
    }

    //Confirma la entrega de recurso y actualizamos reporte

    public void confirmar(){
        this.estado = EstadoAsignacion.CONFIRMADA;
        this.fechaConfirmacion = LocalDateTime.now();
        //Actualizar contador
        this.reporteNecesidad.registrarAtencion(this.cantidadAsignada);
    }

    public void cancelar(String motivo){
        this.estado = EstadoAsignacion.CANCELADA;
        this.observaciones = motivo;
    }

    public enum EstadoAsignacion{
        PLANIFICADA,
        EN_TRANSITO,
        CONFIRMADA,
        CANCELADA
    }



}
