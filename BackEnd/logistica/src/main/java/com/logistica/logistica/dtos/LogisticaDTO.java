package com.logistica.logistica.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.logistica.logistica.entities.Envio.EstadoEnvio;
import com.logistica.logistica.entities.ItemInventario.TipoRecurso;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//Cada clase es public y estática par poder importarse individualemente en los controladores y servicios sin necesidad de importar toda la clase LogisticaDTO
public class LogisticaDTO {


    //-------------------- Request DTOs --------------------

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CentroAcopioRequest{

        @NotBlank(message = "El nombre es obligatorio")
        private String nombre;

        @NotBlank(message = "La dirección es obligatoria")
        private String direccion;

        @NotBlank
        private String region;
        @NotBlank
        private String comuna;

        private Double latitud;
        private Double longitud;
        private String telefono;
        private String emailEncargado;
        private String nombreEncargado;
        private Integer capacidadMaximaKg;
    }


    @Getter
    @Setter
    @NoArgsConstructor
    public static class EnvioRequest {

        @NotNull(message = "El ID del centro de origen es obligatorio")
        private Long centroOrigenId;

        @NotBlank(message = "El destino es obligatoria")
        private String destinoDescripcion;

        @NotBlank
        private String destinoDireccion;

        private String destinoRegion;
        private String destinoComuna;
        private String transportistaNombre;
        private String transportistaRut;
        private String patenteVehiculo;
        private LocalDate fechaPlanificada;
        private String observaciones;

        @NotEmpty(message = "El envío debe tener al menos un recurso")
        @Valid
        private List<DetalleRequest> detalles;

    }


    @Getter
    @Setter
    @NoArgsConstructor
    public static class DetalleRequest {

        @NotNull
        private TipoRecurso tipoRecurso;

        @NotNull
        @DecimalMin("0.01")
        private BigDecimal cantidad;

        @NotBlank
        private String unidadMedida;

        private String descripcion;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ActualizarEstadoRequest {

        @NotNull(message = "Elestado es obligatorio")
        private EstadoEnvio nuevoEstado;

        private String observaciones;

    }



    //-------------------- Response DTOs --------------------

    @Getter
    @Builder
    public static class CentroAcopioResponse {

        private Long id;
        private String nombre;
        private String direccion;
        private String region;
        private String comuna;
        private Double latitud;
        private Double longitud;
        private String estado;
        private Integer capacidadMaximaKg;
        private List<InventarioResponse> inventario;

    }

    @Getter
    @Builder
    public static class InventarioResponse {

        private Long id;
        private TipoRecurso tipoRecurso;
        private BigDecimal cantidadDisponible;
        private String unidadMedida;
        private LocalDateTime ultimaActualizacion;

    }

    @Getter
    @Builder
    public static class EnvioResponse {

        private Long id;
        private String numeroSeguimiento;
        private Long centroOrigenId;
        private String centroOrigenNombre;
        private String destinoDescripcion;
        private String destinoDireccion;
        private String destinoRegion;
        private String destinoComuna;
        private String transportistaNombre;
        private String patenteVehiculo;
        private String estado;
        private LocalDate fechaPlanificada;
        private LocalDateTime fechaDespacho;
        private LocalDateTime fechaEntrega;
        private String observaciones;
        private List<DetalleRequest> detalles;
        private LocalDateTime fechaCreacion;
        

    }


}
