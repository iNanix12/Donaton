package com.donaton.necesidades.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.donaton.necesidades.entities.ReporteNecesidad;
import com.donaton.necesidades.entities.ReporteNecesidad.EstadoReporte;
import com.donaton.necesidades.entities.ReporteNecesidad.TipoRecurso;

@Repository
public interface ReporteNecesidadRepository extends JpaRepository<ReporteNecesidad, Long> {

    //Reporte de una zona, ordenados por prioridad descendente
    @Query("SELECT r FROM ReporteNecesidad r WHERE r.zonaAfectada.id = :zonaId " +
           "ORDER BY r.prioridad ASC, r.fechaReporte DESC")
    List<ReporteNecesidad> findByZonaAfectadaIdOrderByPrioridad(@Param("zonaId") Long zonaId);

    Page<ReporteNecesidad> findByEstadoOrderByPrioridadAscFechaReporteDesc(
        TipoRecurso tipo, EstadoReporte estado, Pageable pageable);

    Page<ReporteNecesidad> findByTipoRecursoNecesarioAndEstadoOrderByPrioridadAsc(
        TipoRecurso tipo, EstadoReporte estado, Pageable pageable);

    //Panel Admin: todos los reportes criticos y altos pendientes
    @Query("SELECT r FROM ReporteNecesidad r " +
           "WHERE r.prioridad IN ('CRITICA', 'ALTA') " +
           "AND r.estado IN ('PENDIENTE', 'EN_GESTION', 'PARCIALMENTE_ATENDIDA') " +
           "ORDER BY r.prioridad ASC, r.fechaReporte ASC")
    List<ReporteNecesidad> findUrgentes();

    //Carga el reporte con sus asignaciones para eviter N + 1
    @Query("SELECT r FROM ReporteNecesidad r LEFT JOIN FETCH r.asignaciones WHERE r.id = :id")
    Optional<ReporteNecesidad> findByIdWithAsignaciones(@Param("id") Long id);

    long countByEstado(EstadoReporte estado);
    long countByZonaAfectadaIdAndEstado(Long zonaId, EstadoReporte estado);

    

}
