package com.donacion.donacion.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para el microservicio de donaciones.
 *
 * Centraliza los errores y devuelve respuestas JSON consistentes al API Gateway
 * (y por consiguiente al frontend), en lugar de exponer stack traces o mensajes
 * internos de Spring.
 */
@RestControllerAdvice
@Slf4j
public class AppConfig {

    /**
     * Maneja errores de validación de campos (@Valid).
     * Devuelve un mapa con el campo que falló y el mensaje de error.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidacionException(
            MethodArgumentNotValidException ex) {

        Map<String, String> erroresCampos = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String campo = ((FieldError) error).getField();
            String mensaje = error.getDefaultMessage();
            erroresCampos.put(campo, mensaje);
        });

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("timestamp", LocalDateTime.now().toString());
        respuesta.put("status", HttpStatus.BAD_REQUEST.value());
        respuesta.put("error", "Datos de entrada inválidos");
        respuesta.put("campos", erroresCampos);

        log.warn("Error de validación: {}", erroresCampos);
        return ResponseEntity.badRequest().body(respuesta);
    }

    /**
     * Maneja errores de lógica de negocio (donante no encontrado, tipo inválido, etc.).
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(
            IllegalArgumentException ex) {

        Map<String, Object> respuesta = Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", HttpStatus.BAD_REQUEST.value(),
                "error", ex.getMessage()
        );

        log.warn("Error de argumento: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(respuesta);
    }

    /**
     * Captura cualquier excepción no controlada para evitar exponer
     * detalles internos al cliente.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        Map<String, Object> respuesta = Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "error", "Error interno del servidor. Intente más tarde."
        );

        log.error("Error no controlado: ", ex);
        return ResponseEntity.internalServerError().body(respuesta);
    }
}