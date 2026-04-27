package com.donaton.necesidades.controllers;

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

import com.donaton.necesidades.dtos.NecesidadDTO.ActualizarEstadoRequest;
import com.donaton.necesidades.dtos.NecesidadDTO.AsignacionRecursoRequest;
import com.donaton.necesidades.dtos.NecesidadDTO.AsignacionRecursoResponse;
import com.donaton.necesidades.dtos.NecesidadDTO.ReporteNecesidadRequest;
import com.donaton.necesidades.dtos.NecesidadDTO.ReporteNecesidadResponse;
import com.donaton.necesidades.dtos.NecesidadDTO.ResumenGeneralResponse;
import com.donaton.necesidades.dtos.NecesidadDTO.ZonaAfectadaRequest;
import com.donaton.necesidades.dtos.NecesidadDTO.ZonaAfectadaResponse;
import com.donaton.necesidades.services.NecesidadService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/necesidades")
@RequiredArgsConstructor
@Slf4j
public class NecesidadController {

    private final NecesidadService necesidadService;

    //------------------------Zonas Afectadas-----------------------

    @PostMapping("/zonas")
    public ResponseEntity<ZonaAfectadaResponse> crearZona(
        @Valid @RequestBody ZonaAfectadaRequest request){
            log.info("POST /api/necesidades/zonas - nombre: {}", request.getNombre());
            return ResponseEntity.status(HttpStatus.CREATED)
            .body(necesidadService.crearZona(request));
        }

    @GetMapping("/zonas/{id}")
    public ResponseEntity<ZonaAfectadaResponse> obtenerZona(@PathVariable Long id) {
        return ResponseEntity.ok(necesidadService.obtenerZonaPorId(id));
    }


    @GetMapping("/zonas")
    public ResponseEntity<List<ZonaAfectadaResponse>> listarZonasActivas() {
        return ResponseEntity.ok(necesidadService.listarZonasActivas());
    }

    @GetMapping("/zonas/region/{region}")
    public ResponseEntity<List<ZonaAfectadaResponse>> listarPorRegion(
            @PathVariable String region) {
        return ResponseEntity.ok(necesidadService.listarZonasPorRegion(region));
    }

    @PatchMapping("/zonas/{id}/cerrar")
    public ResponseEntity<ZonaAfectadaResponse> cerrarZona(@PathVariable Long id) {
        log.info("PATCH /api/necesidades/zonas/{}/cerrar", id);
        return ResponseEntity.ok(necesidadService.cerrarZona(id));
    }


    //-----------------Reporte de Necesidad-------------------


    @PostMapping("/reportes")
    public ResponseEntity<ReporteNecesidadResponse> crearReporte(
            @Valid @RequestBody ReporteNecesidadRequest request) {
        log.info("POST /api/necesidades/reportes - zona: {}, prioridad: {}",
                request.getZonaAfectadaId(), request.getPrioridad());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(necesidadService.crearReporte(request));
    }
 
    @GetMapping("/reportes/{id}")
    public ResponseEntity<ReporteNecesidadResponse> obtenerReporte(@PathVariable Long id) {
        return ResponseEntity.ok(necesidadService.obtenerReportePorId(id));
    }
 
    @GetMapping("/reportes/zona/{zonaId}")
    public ResponseEntity<List<ReporteNecesidadResponse>> listarReportesPorZona(
            @PathVariable Long zonaId) {
        return ResponseEntity.ok(necesidadService.listarReportesPorZona(zonaId));
    }



    //Panel del Administrador: reportes urgentes (CRITICA + ALTA) aún no atendidos


    @GetMapping("/reportes/urgentes")
    public ResponseEntity<List<ReporteNecesidadResponse>> listarUrgentes() {
        return ResponseEntity.ok(necesidadService.listarReportesUrgentes());
    }
 
    @GetMapping("/reportes")
    public ResponseEntity<Page<ReporteNecesidadResponse>> listarTodos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by("prioridad").ascending().and(Sort.by("fechaReporte").descending()));
        return ResponseEntity.ok(necesidadService.listarTodosLosReportes(pageable));
    }
 
    @PatchMapping("/reportes/{id}/estado")
    public ResponseEntity<ReporteNecesidadResponse> actualizarEstado(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarEstadoRequest request) {
        log.info("PATCH /api/necesidades/reportes/{}/estado - nuevoEstado: {}",
                id, request.getNuevoEstado());
        return ResponseEntity.ok(necesidadService.actualizarEstadoReporte(id, request));
    }


    //---------------------Asignación de Recursos-----------------------------


    @PostMapping("/asignaciones")
    public ResponseEntity<AsignacionRecursoResponse> crearAsignacion(
            @Valid @RequestBody AsignacionRecursoRequest request) {
        log.info("POST /api/necesidades/asignaciones - reporte: {}", request.getReporteNecesidadId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(necesidadService.crearAsignacion(request));
    }
 
    @PatchMapping("/asignaciones/{id}/confirmar")
    public ResponseEntity<AsignacionRecursoResponse> confirmarAsignacion(@PathVariable Long id) {
        log.info("PATCH /api/necesidades/asignaciones/{}/confirmar", id);
        return ResponseEntity.ok(necesidadService.confirmarAsignacion(id));
    }
 
    @PatchMapping("/asignaciones/{id}/cancelar")
    public ResponseEntity<AsignacionRecursoResponse> cancelarAsignacion(
            @PathVariable Long id,
            @RequestParam String motivo) {
        log.info("PATCH /api/necesidades/asignaciones/{}/cancelar", id);
        return ResponseEntity.ok(necesidadService.cancelarAsignacion(id, motivo));
    }
 
    @GetMapping("/asignaciones/reporte/{reporteId}")
    public ResponseEntity<List<AsignacionRecursoResponse>> listarAsignaciones(
            @PathVariable Long reporteId) {
        return ResponseEntity.ok(necesidadService.listarAsignacionesPorReporte(reporteId));
    }


    //--------------------Panel Administrador---------------------


    @GetMapping("/admin/resumen")
    public ResponseEntity<ResumenGeneralResponse> obtenerResumen() {
        return ResponseEntity.ok(necesidadService.obtenerResumenGeneral());
    }


    

}
