package com.donacion.donacion.dtos;


import com.donacion.donacion.entities.RecursoDonado.TipoRecurso;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Getter
@Builder
public class DonacionResponseDTO {

    private Long recursoId;

    // Datos del donante confirmados
    private Long donanteId;
    private String rutDonante;
    private String nombreDonante;
    private String tipoDonante;
    private boolean donanteNuevo; // true si fue creado en esta operación

    // Datos del recurso registrado
    private TipoRecurso tipoRecurso;
    private BigDecimal cantidad;
    private String unidadMedida;
    private String descripcion;
    private Long centroAcopioId;
    private LocalDateTime fechaDonacion;

    private String mensaje;
}