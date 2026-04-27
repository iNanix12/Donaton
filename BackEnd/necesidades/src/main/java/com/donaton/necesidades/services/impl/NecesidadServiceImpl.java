package com.donaton.necesidades.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import org.springframework.stereotype.Service;

import com.donaton.necesidades.dtos.NecesidadDTO.ActualizarEstadoRequest;
import com.donaton.necesidades.dtos.NecesidadDTO.AsignacionRecursoRequest;
import com.donaton.necesidades.dtos.NecesidadDTO.AsignacionRecursoResponse;
import com.donaton.necesidades.dtos.NecesidadDTO.ReporteNecesidadRequest;
import com.donaton.necesidades.dtos.NecesidadDTO.ReporteNecesidadResponse;
import com.donaton.necesidades.dtos.NecesidadDTO.ResumenGeneralResponse;
import com.donaton.necesidades.dtos.NecesidadDTO.ZonaAfectadaRequest;
import com.donaton.necesidades.dtos.NecesidadDTO.ZonaAfectadaResponse;
import com.donaton.necesidades.entities.AsignacionRecurso;
import com.donaton.necesidades.entities.AsignacionRecurso.EstadoAsignacion;
import com.donaton.necesidades.entities.ReporteNecesidad;
import com.donaton.necesidades.entities.ReporteNecesidad.EstadoReporte;
import com.donaton.necesidades.entities.ZonaAfectada;
import com.donaton.necesidades.entities.ZonaAfectada.EstadoZona;
import com.donaton.necesidades.repositories.AsignacionRecursoRepository;
import com.donaton.necesidades.repositories.ReporteNecesidadRepository;
import com.donaton.necesidades.repositories.ZonaAfectadaRepository;
import com.donaton.necesidades.services.NecesidadService;

import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NecesidadServiceImpl implements NecesidadService {

    private final ZonaAfectadaRepository zonaRepo;
    private final ReporteNecesidadRepository reporteRepo;
    private final AsignacionRecursoRepository asignacionRepo;


    //-------------------------Zonas Afectadas----------------------------

    @Override
    @Transactional
    public ZonaAfectadaResponse crearZona(ZonaAfectadaRequest request){
        ZonaAfectada zona = new ZonaAfectada();
        zona.setNombre(request.getNombre());
        zona.setDescripcion(request.getDescripcion());
        zona.setRegion(request.getRegion());
        zona.setComuna(request.getComuna());
        zona.setDireccionReferencia(request.getDireccionReferencia());
        zona.setLatitud(request.getLatitud());
        zona.setLongitud(request.getLongitud());
        zona.setTipoEmergencia(request.getTipoEmergencia());
        zona.setPersonasAfectadas(request.getPersonasAfectadas());
        zona.setCoordinadorNombre(request.getCoordinadorNombre());
        zona.setCoordinadorTelefono(request.getCoordinadorTelefono());
 
        ZonaAfectada guardada = zonaRepo.save(zona);
        log.info("Zona afectada creada: id={}, nombre={}", guardada.getId(), guardada.getNombre());
        return mapearZona(guardada);

    }

    @Override
    @Transactional(readOnly = true)
    public ZonaAfectadaResponse obtenerZonaPorId(Long id) {
        ZonaAfectada zona = zonaRepo.findByIdWithReportes(id)
                .orElseThrow(() -> new IllegalArgumentException("Zona afectada no encontrada: " + id));
        return mapearZona(zona);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ZonaAfectadaResponse> listarZonasActivas() {
        return zonaRepo.findByEstadoOrderByFechaAperturaDesc(EstadoZona.ACTIVA)
                .stream().map(this::mapearZona).collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public List<ZonaAfectadaResponse> listarZonasPorRegion(String region) {
        return zonaRepo.findByRegionIgnoreCaseAndEstadoOrderByFechaAperturaDesc(region, EstadoZona.ACTIVA)
                .stream().map(this::mapearZona).collect(Collectors.toList());
    }


    @Override
    @Transactional
    public ZonaAfectadaResponse cerrarZona(Long id) {
        ZonaAfectada zona = zonaRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Zona afectada no encontrada: " + id));
 
        long pendientes = reporteRepo.countByZonaAfectadaIdAndEstado(id, EstadoReporte.PENDIENTE);
        if (pendientes > 0) {
            throw new IllegalStateException(
                    "No se puede cerrar la zona: tiene " + pendientes + " reporte(s) aún pendientes.");
        }
 
        zona.cerrar();
        log.info("Zona cerrada: id={}", id);
        return mapearZona(zonaRepo.save(zona));
    }


    //---------------------Reportes de Necesidad-------------------------



    @Override
    @Transactional
    public ReporteNecesidadResponse crearReporte(ReporteNecesidadRequest request) {
        ZonaAfectada zona = zonaRepo.findById(request.getZonaAfectadaId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Zona afectada no encontrada: " + request.getZonaAfectadaId()));
 
        if (zona.getEstado() == EstadoZona.CERRADA) {
            throw new IllegalStateException("No se pueden agregar reportes a una zona cerrada.");
        }
 
        ReporteNecesidad reporte = new ReporteNecesidad();
        reporte.setZonaAfectada(zona);
        reporte.setTitulo(request.getTitulo());
        reporte.setDescripcion(request.getDescripcion());
        reporte.setTipoRecursoNecesario(request.getTipoRecursoNecesario());
        reporte.setCantidadRequerida(request.getCantidadRequerida());
        reporte.setUnidadMedida(request.getUnidadMedida());
        reporte.setPrioridad(request.getPrioridad());
        reporte.setReportadoPor(request.getReportadoPor());
        reporte.setTelefonoContacto(request.getTelefonoContacto());
        reporte.setObservaciones(request.getObservaciones());
 
        ReporteNecesidad guardado = reporteRepo.save(reporte);
        log.info("Reporte creado: id={}, zona={}, prioridad={}",
                guardado.getId(), zona.getNombre(), guardado.getPrioridad());
        return mapearReporte(guardado);
    }



    @Override
    @Transactional(readOnly = true)
    public ReporteNecesidadResponse obtenerReportePorId(Long id) {
        ReporteNecesidad reporte = reporteRepo.findByIdWithAsignaciones(id)
                .orElseThrow(() -> new IllegalArgumentException("Reporte no encontrado: " + id));
        return mapearReporte(reporte);
    }



     @Override
    @Transactional(readOnly = true)
    public List<ReporteNecesidadResponse> listarReportesPorZona(Long zonaId) {
        return reporteRepo.findByZonaAfectadaIdOrderByPrioridad(zonaId)
                .stream().map(this::mapearReporte).collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public List<ReporteNecesidadResponse> listarReportesUrgentes() {
        return reporteRepo.findUrgentes()
                .stream().map(this::mapearReporte).collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public Page<ReporteNecesidadResponse> listarTodosLosReportes(Pageable pageable) {
        return reporteRepo.findAll(pageable).map(this::mapearReporte);
    }



    @Override
    @Transactional
    public ReporteNecesidadResponse actualizarEstadoReporte(Long id, ActualizarEstadoRequest request) {
        ReporteNecesidad reporte = reporteRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reporte no encontrado: " + id));
 
        reporte.setEstado(request.getNuevoEstado());
        if (request.getObservaciones() != null) {
            reporte.setObservaciones(request.getObservaciones());
        }
 
        log.info("Estado del reporte {} actualizado a: {}", id, request.getNuevoEstado());
        return mapearReporte(reporteRepo.save(reporte));
    }


    //-------------------Asignaciones----------------------


    @Override
    @Transactional
    public AsignacionRecursoResponse crearAsignacion(AsignacionRecursoRequest request) {
        ReporteNecesidad reporte = reporteRepo.findById(request.getReporteNecesidadId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Reporte no encontrado: " + request.getReporteNecesidadId()));
 
        if (reporte.getEstado() == EstadoReporte.ATENDIDA
                || reporte.getEstado() == EstadoReporte.CANCELADA) {
            throw new IllegalStateException(
                    "No se puede asignar recursos a un reporte en estado: " + reporte.getEstado());
        }
 
        AsignacionRecurso asignacion = new AsignacionRecurso();
        asignacion.setReporteNecesidad(reporte);
        asignacion.setEnvioIdLogistica(request.getEnvioIdLogistica());
        asignacion.setNumeroSeguimientoEnvio(request.getNumeroSeguimientoEnvio());
        asignacion.setCantidadAsignada(request.getCantidadAsignada());
        asignacion.setUnidadMedida(request.getUnidadMedida());
        asignacion.setAsignadoPor(request.getAsignadoPor());
        asignacion.setObservaciones(request.getObservaciones());
 
        // Actualiza el estado del reporte a EN_GESTION si estaba PENDIENTE
        if (reporte.getEstado() == EstadoReporte.PENDIENTE) {
            reporte.setEstado(EstadoReporte.EN_GESTION);
            reporteRepo.save(reporte);
        }
 
        AsignacionRecurso guardada = asignacionRepo.save(asignacion);
        log.info("Asignación creada: id={}, reporte={}, cantidad={}",
                guardada.getId(), reporte.getId(), guardada.getCantidadAsignada());
        return mapearAsignacion(guardada);
    }



    @Override
    @Transactional
    public AsignacionRecursoResponse confirmarAsignacion(Long asignacionId) {
        AsignacionRecurso asignacion = asignacionRepo.findById(asignacionId)
                .orElseThrow(() -> new IllegalArgumentException("Asignación no encontrada: " + asignacionId));
 
        if (asignacion.getEstado() == EstadoAsignacion.CONFIRMADA) {
            throw new IllegalStateException("La asignación ya fue confirmada.");
        }
 
        // confirmar() actualiza cantidadAtendida en el reporte padre (lógica en entidad)
        asignacion.confirmar();
        asignacionRepo.save(asignacion);
        reporteRepo.save(asignacion.getReporteNecesidad());
 
        log.info("Asignación confirmada: id={}, reporte={}",
                asignacionId, asignacion.getReporteNecesidad().getId());
        return mapearAsignacion(asignacion);
    }




    @Override
    @Transactional
    public AsignacionRecursoResponse cancelarAsignacion(Long asignacionId, String motivo) {
        AsignacionRecurso asignacion = asignacionRepo.findById(asignacionId)
                .orElseThrow(() -> new IllegalArgumentException("Asignación no encontrada: " + asignacionId));
 
        asignacion.cancelar(motivo);
        log.info("Asignación cancelada: id={}, motivo={}", asignacionId, motivo);
        return mapearAsignacion(asignacionRepo.save(asignacion));
    }



    @Override
    @Transactional(readOnly = true)
    public List<AsignacionRecursoResponse> listarAsignacionesPorReporte(Long reporteId) {
        return asignacionRepo.findByReporteNecesidadIdOrderByFechaAsignacionDesc(reporteId)
                .stream().map(this::mapearAsignacion).collect(Collectors.toList());
    }
 
    @Override
    @Transactional(readOnly = true)
    public ResumenGeneralResponse obtenerResumenGeneral() {
        return ResumenGeneralResponse.builder()
                .totalZonasActivas(zonaRepo.countByEstado(EstadoZona.ACTIVA))
                .totalReportesPendientes(reporteRepo.countByEstado(EstadoReporte.PENDIENTE))
                .totalReportesEnGestion(reporteRepo.countByEstado(EstadoReporte.EN_GESTION))
                .totalReportesAtendidos(reporteRepo.countByEstado(EstadoReporte.ATENDIDA))
                .totalAsignacionesPlanificadas(asignacionRepo.countByEstado(EstadoAsignacion.PLANIFICADA))
                .totalAsignacionesConfirmadas(asignacionRepo.countByEstado(EstadoAsignacion.CONFIRMADA))
                .build();
    }




    //------------------------Mapear privados----------------------------



    private ZonaAfectadaResponse mapearZona(ZonaAfectada z) {
        long pendientes = z.getReportes().stream()
                .filter(r -> r.getEstado() == EstadoReporte.PENDIENTE).count();
        long atendidos = z.getReportes().stream()
                .filter(r -> r.getEstado() == EstadoReporte.ATENDIDA).count();
 
        return ZonaAfectadaResponse.builder()
                .id(z.getId())
                .nombre(z.getNombre())
                .descripcion(z.getDescripcion())
                .region(z.getRegion())
                .comuna(z.getComuna())
                .direccionReferencia(z.getDireccionReferencia())
                .latitud(z.getLatitud())
                .longitud(z.getLongitud())
                .tipoEmergencia(z.getTipoEmergencia().name())
                .estado(z.getEstado().name())
                .personasAfectadas(z.getPersonasAfectadas())
                .coordinadorNombre(z.getCoordinadorNombre())
                .coordinadorTelefono(z.getCoordinadorTelefono())
                .fechaApertura(z.getFechaApertura())
                .fechaCierre(z.getFechaCierre())
                .totalReportes(z.getReportes().size())
                .reportesPendientes(pendientes)
                .reportesAtendidos(atendidos)
                .build();
    }




     private ReporteNecesidadResponse mapearReporte(ReporteNecesidad r) {
        List<AsignacionRecursoResponse> asignaciones = r.getAsignaciones().stream()
                .map(this::mapearAsignacion).collect(Collectors.toList());
 
        return ReporteNecesidadResponse.builder()
                .id(r.getId())
                .zonaAfectadaId(r.getZonaAfectada().getId())
                .zonaAfectadaNombre(r.getZonaAfectada().getNombre())
                .titulo(r.getTitulo())
                .descripcion(r.getDescripcion())
                .tipoRecursoNecesario(r.getTipoRecursoNecesario().name())
                .cantidadRequerida(r.getCantidadRequerida())
                .cantidadAtendida(r.getCantidadAtendida())
                .unidadMedida(r.getUnidadMedida())
                .porcentajeAtencion(r.getPorcentajeAtencion())
                .prioridad(r.getPrioridad().name())
                .estado(r.getEstado().name())
                .reportadoPor(r.getReportadoPor())
                .telefonoContacto(r.getTelefonoContacto())
                .observaciones(r.getObservaciones())
                .fechaReporte(r.getFechaReporte())
                .fechaUltimaActualizacion(r.getFechaUltimaActualizacion())
                .asignaciones(asignaciones)
                .build();
    }



    private AsignacionRecursoResponse mapearAsignacion(AsignacionRecurso a) {
        return AsignacionRecursoResponse.builder()
                .id(a.getId())
                .reporteNecesidadId(a.getReporteNecesidad().getId())
                .tituloReporte(a.getReporteNecesidad().getTitulo())
                .envioIdLogistica(a.getEnvioIdLogistica())
                .numeroSeguimientoEnvio(a.getNumeroSeguimientoEnvio())
                .cantidadAsignada(a.getCantidadAsignada())
                .unidadMedida(a.getUnidadMedida())
                .estado(a.getEstado().name())
                .asignadoPor(a.getAsignadoPor())
                .observaciones(a.getObservaciones())
                .fechaAsignacion(a.getFechaAsignacion())
                .fechaConfirmacion(a.getFechaConfirmacion())
                .build();
    }



}
