package com.donaton.necesidades.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.donaton.necesidades.entities.ReporteNecesidad.EstadoReporte;
import com.donaton.necesidades.entities.ReporteNecesidad.Prioridad;
import com.donaton.necesidades.entities.ReporteNecesidad.TipoRecurso;
import com.donaton.necesidades.entities.ZonaAfectada.TipoEmergencia;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//Clase contenedora de DTOs de Necesidades (Agrupamos Request y Response)
public class NecesidadDTO {

    //----------------------------------RequestDTOs--------------------------------

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ZonaAfectadaRequest {

        @NotBlank(message = "El nombre de la zona es obligatorio")
        @Size(max = 200)
        private String nombre;

        @Size(max = 500)
        private String descripcion;

        @NotBlank(message = "La región es obligatoria")
        private String region;

        @NotBlank(message = "La comuna es obligatoria")
        private String comuna;

        @Size(max = 300)
        private String direccionReferencia;

        private Double latitud;
        private Double longitud;

        @NotNull(message = "El tipo de emergencia es obligatorio")
        private TipoEmergencia tipoEmergencia;

        @Min(value = 0, message = "El número de personas afectadas no puede ser negativo")
        private Integer personasAfectadas;

        @Size(max = 150)
        private String coordinadorNombre;

        @Size(max = 20)
        private String coordinadorTelefono;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ReporteNecesidadRequest {

        @NotNull(message = "El ID de la zona afectada es obligatorio")
        private Long zonaAfectadaId;

        @NotBlank(message = "El título es obligatorio")
        @Size(max = 200)
        private String titulo;

        @NotBlank (message = "La descripción es obligatoria")
        @Size (max = 1000)
        private String descripcion;

        @NotNull(message = "El tipo de recurso necesario es obligatorio")
        private TipoRecurso tipoRecursoNecesario;

        @NotNull(message = "La cantidad requerida es obligatoria")
        @DecimalMin(value = "0.01", message = "La cantidad debe ser mayor a 0")
        private BigDecimal cantidadRequerida;

        @NotBlank(message = "La unidad de medida es obligatoria")
        @Size (max = 30)
        private String unidadMedida;

        private Prioridad prioridad = Prioridad.MEDIA;

        @NotBlank(message = "El nombre de quien reporta es obligatorio")
        @Size(max = 150)
        private String reportadoPor;

        @Size(max = 20)
        private String telefonoContacto;

        @Size(max = 500)
        private String observaciones;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class AsignacionRecursoRequest{

        @NotNull(message =  "El ID del reporte es obligatorio")
        private Long reporteNecesidadId;

        //Id del envío en el microservicio de Logistica (opcional al crear)
        private Long envioIdLogistica;
        private String numeroSeguimientoEnvio;

        @NotNull(message = "La cantidad asignada es obligatoria")
        @DecimalMin(value = "0.01")
        private BigDecimal cantidadAsignada;

        @NotBlank
        @Size (max = 30)
        private String unidadMedida;

        @NotBlank(message = "El nombre de quien asigna es obligatorio")
        @Size(max = 150)
        private String asignadoPor;

        @Size(max = 500)
        private String observaciones;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ActualizarEstadoRequest{
        @NotNull(message = "El estado es obligatorio")
        private EstadoReporte nuevoEstado;

        @Size(max = 500)
        private String observaciones;

    }


    //----------------------------------ResponseDTOs---------------------------------------

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ZonaAfectadaResponse {
        private Long id;
        private String nombre;
        private String descripcion;
        private String region;
        private String comuna;
        private String direccionReferencia;
        private Double latitud;
        private Double longitud;
        private String tipoEmergencia;
        private String estado;
        private Integer personasAfectadas;
        private String coordinadorNombre;
        private String coordinadorTelefono;
        private LocalDateTime fechaApertura;
        private LocalDateTime fechaCierre;
        private int totalReportes;
        private long reportesPendientes;
        private long reportesAtendidos;
    }
 
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReporteNecesidadResponse {
        private Long id;
        private Long zonaAfectadaId;
        private String zonaAfectadaNombre;
        private String titulo;
        private String descripcion;
        private String tipoRecursoNecesario;
        private BigDecimal cantidadRequerida;
        private BigDecimal cantidadAtendida;
        private String unidadMedida;
        private int porcentajeAtencion;
        private String prioridad;
        private String estado;
        private String reportadoPor;
        private String telefonoContacto;
        private String observaciones;
        private LocalDateTime fechaReporte;
        private LocalDateTime fechaUltimaActualizacion;
        private List<AsignacionRecursoResponse> asignaciones;
    }
 
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AsignacionRecursoResponse {
        private Long id;
        private Long reporteNecesidadId;
        private String tituloReporte;
        private Long envioIdLogistica;
        private String numeroSeguimientoEnvio;
        private BigDecimal cantidadAsignada;
        private String unidadMedida;
        private String estado;
        private String asignadoPor;
        private String observaciones;
        private LocalDateTime fechaAsignacion;
        private LocalDateTime fechaConfirmacion;
    }
 
    
     //DTO de resumen estadístico para el panel del administrador.
     
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResumenGeneralResponse {
        private long totalZonasActivas;
        private long totalReportesPendientes;
        private long totalReportesEnGestion;
        private long totalReportesAtendidos;
        private long totalAsignacionesPlanificadas;
        private long totalAsignacionesConfirmadas;
    }



}
