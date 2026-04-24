package com.donacion.donacion.dtos;


import com.donacion.donacion.entities.RecursoDonado.TipoRecurso;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class DonacionRequestDTO {

    // ── Datos del donante ────────────────────────────────────────────────

    @NotBlank(message = "El tipo de donante es obligatorio")
    @Pattern(regexp = "PARTICULAR|EMPRESA", message = "Tipo de donante debe ser PARTICULAR o EMPRESA")
    private String tipoDonante;


    @NotBlank(message = "El RUT es obligatorio")
    @Pattern(regexp = "^\\d{1,2}\\.?\\d{3}\\.?\\d{3}-[\\dkK]$",
             message = "El RUT debe tener un formato válido, ej: 12345678-9")
    private String rut;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no tiene un formato válido")
    private String email;

    @Size(max = 20, message = "El teléfono no debe superar 20 caracteres")
    private String telefono;

    @Size(max = 255, message = "La dirección no debe superar 255 caracteres")
    private String direccion;

    // ── Campos exclusivos para PARTICULAR ───────────────────────────────

    @Size(max = 100, message = "Los nombres no deben superar 100 caracteres")
    private String nombres;

    @Size(max = 100, message = "Los apellidos no deben superar 100 caracteres")
    private String apellidos;

    // ── Campos exclusivos para EMPRESA ──────────────────────────────────

    @Size(max = 200, message = "La razón social no debe superar 200 caracteres")
    private String razonSocial;

    @Size(max = 150, message = "El nombre de contacto no debe superar 150 caracteres")
    private String nombreContacto;

    @Size(max = 150, message = "El giro no debe superar 150 caracteres")
    private String giro;

    // ── Datos del recurso donado ─────────────────────────────────────────

    @NotNull(message = "El tipo de recurso es obligatorio")
    private TipoRecurso tipoRecurso;

    @NotNull(message = "La cantidad es obligatoria")
    @DecimalMin(value = "0.01", message = "La cantidad debe ser mayor a 0")
    private BigDecimal cantidad;

    @NotBlank(message = "La unidad de medida es obligatoria")
    @Size(max = 30)
    private String unidadMedida;

    @Size(max = 500, message = "La descripción no debe superar 500 caracteres")
    private String descripcion;

    /** ID del centro de acopio destino (puede ser null si no se asigna aún). */
    private Long centroAcopioId;

    @Size(max = 500, message = "Las observaciones no deben superar 500 caracteres")
    private String observaciones;
}