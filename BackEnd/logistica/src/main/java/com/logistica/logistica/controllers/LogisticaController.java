package com.logistica.logistica.controllers;

import java.math.BigDecimal;
import java.util.List;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.logistica.logistica.dtos.LogisticaDTO.ActualizarEstadoRequest;
import com.logistica.logistica.dtos.LogisticaDTO.CentroAcopioRequest;
import com.logistica.logistica.dtos.LogisticaDTO.CentroAcopioResponse;
import com.logistica.logistica.dtos.LogisticaDTO.EnvioRequest;
import com.logistica.logistica.dtos.LogisticaDTO.EnvioResponse;
import com.logistica.logistica.services.LogisticaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/logistica")
@RequiredArgsConstructor
@Slf4j
public class LogisticaController {

    private final LogisticaService logisticaService;

    //-----------------------Centros de Acopio------------------------

    @PostMapping("centros")
    public ResponseEntity<CentroAcopioResponse> crearCentro(
        @Valid @RequestBody CentroAcopioRequest request) {
            log.info("POST /api/logistica/centros - nombre: {}", request.getNombre());
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(logisticaService.crearCentroAcopio(request));
        }  

    @GetMapping("centros/{id}")
    public ResponseEntity<CentroAcopioResponse> obtenerCentro(@PathVariable Long id){
        return ResponseEntity.ok(logisticaService.obtenerCentroPorId(id));
    }

    @GetMapping("/centros")
    public ResponseEntity<List<CentroAcopioResponse>> listarPorRegion(
        @PathVariable String region) {
        return ResponseEntity.ok(logisticaService.listarCentrosPorRegion(region));
    }



    //-----------------------Inventario------------------------
    //Endpoint consumido internamente por el microservicio de Donaciones (vía gateway + webClient)

    @PostMapping("/inventario/recibir/{centroId}")
    public ResponseEntity<Void> recibirDonacion(
        @PathVariable Long centroId,
        @RequestParam String tipoRecurso,
        @RequestParam BigDecimal cantidad,
        @RequestParam String unidadMedida){

            log.info("POST /inventario/recibir/{} - tipo={}, cantidad ={}", centroId, tipoRecurso, cantidad);
            logisticaService.recibirDonacion(centroId, tipoRecurso, cantidad, unidadMedida);
            return ResponseEntity.ok().build();
        }



    //-----------------------Envíos------------------------

    @PostMapping("/envios")
    public ResponseEntity<EnvioResponse> planificarEnvio(
             @Valid @RequestBody EnvioRequest request) {
        log.info("POST /api/logistica/envios - centroOrigen: {}", request.getCentroOrigenId());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(logisticaService.planificarEnvio(request));

             }


    @PatchMapping("/envios/{id}/despachar")
    public ResponseEntity<EnvioResponse> despacharEnvio(@PathVariable Long id) {
        log.info("PATCH /api/logistica/envios/{}/despachar", id);
        return ResponseEntity.ok(logisticaService.despacharEnvio(id));
    }



    @PatchMapping("/envios/{id}/entregar")
    public ResponseEntity<EnvioResponse> confirmarEntrega(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarEstadoRequest request) {
        log.info("PATCH /api/logistica/envios/{}/entregar", id);
        return ResponseEntity.ok(logisticaService.confirmarEntrega(id, request));
    }


    @PatchMapping("/envios/{id}/cancelar")
    public ResponseEntity<EnvioResponse> cancelarEnvio(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarEstadoRequest request) {
        log.info("PATCH /api/logistica/envios/{}/cancelar", id);
        return ResponseEntity.ok(logisticaService.cancelarEnvio(id, request));
    }



    @GetMapping("/envios/centro/{centroId}")
    public ResponseEntity<Page<EnvioResponse>> listarEnviosPorCentro(
            @PathVariable Long centroId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("fechaCreacion").descending());
        return ResponseEntity.ok(logisticaService.listarEnviosPorCentro(centroId, pageable));
    }



    @GetMapping("/envios")
    public ResponseEntity<Page<EnvioResponse>> listarTodosLosEnvios(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("fechaCreacion").descending());
        return ResponseEntity.ok(logisticaService.listarTodosLosEnvios(pageable));
    }
    

     // ── Seguimiento público ───────────────────────────────────────────────
 
    //Permite a cualquier usuario rastrear un envío usando su número de seguimiento, sin necesidad de autenticación
    @GetMapping("/seguimiento/{numeroSeguimiento}")
    public ResponseEntity<EnvioResponse> rastrearEnvio(
            @PathVariable String numeroSeguimiento) {
        log.info("GET /api/logistica/seguimiento/{}", numeroSeguimiento);
        return ResponseEntity.ok(logisticaService.buscarPorNumeroSeguimiento(numeroSeguimiento));
    }
    

    

    

}
