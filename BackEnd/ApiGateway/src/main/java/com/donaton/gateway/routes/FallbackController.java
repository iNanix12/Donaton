package com.donaton.gateway.routes;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Controlador de respuestas fallback del Circuit Breaker.
 *
 * Cuando un microservicio no responde o supera el umbral de errores,
 * Resilience4j redirige la solicitud aquí en lugar de devolver
 * un error 500 genérico al frontend.
 *
 * Cada endpoint devuelve un mensaje descriptivo que permite al frontend
 * mostrar una UI degradada en lugar de una pantalla de error total.
 */
@RestController
@RequestMapping("/fallback")
@Slf4j
public class FallbackController {

    @GetMapping("/donaciones")
    public Mono<ResponseEntity<Map<String, Object>>> fallbackDonaciones() {
        log.warn("Circuit Breaker activado para: donacion-service");
        return Mono.just(ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(buildFallbackBody(
                        "donacion-service",
                        "El servicio de donaciones no está disponible en este momento. " +
                        "Por favor intente más tarde."
                )));
    }

    @GetMapping("/logistica")
    public Mono<ResponseEntity<Map<String, Object>>> fallbackLogistica() {
        log.warn("Circuit Breaker activado para: logistica-service");
        return Mono.just(ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(buildFallbackBody(
                        "logistica-service",
                        "El servicio de logística no está disponible en este momento. " +
                        "Por favor intente más tarde."
                )));
    }

    @GetMapping("/necesidades")
    public Mono<ResponseEntity<Map<String, Object>>> fallbackNecesidades() {
        log.warn("Circuit Breaker activado para: necesidades-service");
        return Mono.just(ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(buildFallbackBody(
                        "necesidades-service",
                        "El servicio de necesidades en terreno no está disponible. " +
                        "Por favor intente más tarde."
                )));
    }

    private Map<String, Object> buildFallbackBody(String servicio, String mensaje) {
        return Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", HttpStatus.SERVICE_UNAVAILABLE.value(),
                "servicio", servicio,
                "mensaje", mensaje,
                "circuitBreaker", "OPEN"
        );
    }
}