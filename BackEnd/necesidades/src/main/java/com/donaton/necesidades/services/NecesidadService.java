package com.donaton.necesidades.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.donaton.necesidades.dtos.NecesidadDTO.ActualizarEstadoRequest;
import com.donaton.necesidades.dtos.NecesidadDTO.AsignacionRecursoRequest;
import com.donaton.necesidades.dtos.NecesidadDTO.AsignacionRecursoResponse;
import com.donaton.necesidades.dtos.NecesidadDTO.ReporteNecesidadRequest;
import com.donaton.necesidades.dtos.NecesidadDTO.ReporteNecesidadResponse;
import com.donaton.necesidades.dtos.NecesidadDTO.ResumenGeneralResponse;
import com.donaton.necesidades.dtos.NecesidadDTO.ZonaAfectadaRequest;
import com.donaton.necesidades.dtos.NecesidadDTO.ZonaAfectadaResponse;

public interface NecesidadService {

    //---------------ZonaAfectada---------------------

    ZonaAfectadaResponse crearZona(ZonaAfectadaRequest request);

    ZonaAfectadaResponse obtenerZonaPorId(Long id);

    List<ZonaAfectadaResponse> listarZonasActivas();

    List<ZonaAfectadaResponse> listarZonasPorRegion(String region);

    ZonaAfectadaResponse cerrarZona(Long id);

    //----------------Reportes de Necesidad---------------

    ReporteNecesidadResponse crearReporte(ReporteNecesidadRequest request);

    ReporteNecesidadResponse obtenerReportePorId(Long id);

    List<ReporteNecesidadResponse> listarReportesPorZona(Long zonaId);

    //Listar todos lso reportes con prioridad CRITICA o ALTA

    List<ReporteNecesidadResponse> listarReportesUrgentes();

    Page<ReporteNecesidadResponse> listarTodosLosReportes(Pageable pageable);

    ReporteNecesidadResponse actualizarEstadoReporte(Long id, ActualizarEstadoRequest request);



    //------------------------Asignaciones de Recursos------------------------
    //Opcional comunica al microservicio de Logística vía WebClient para vincular el envío correspondiente

    AsignacionRecursoResponse crearAsignacion(AsignacionRecursoRequest request);

    AsignacionRecursoResponse confirmarAsignacion(Long asignacionId);

    AsignacionRecursoResponse cancelarAsignacion(Long asignacionId, String motivo);

    List<AsignacionRecursoResponse> listarAsignacionesPorReporte(Long reportId);



    //-------------------------Panel Administrador----------------------------

    ResumenGeneralResponse obtenerResumenGeneral();

}
