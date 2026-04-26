package com.donaton.necesidades.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.donaton.necesidades.entities.AsignacionRecurso;
import com.donaton.necesidades.entities.AsignacionRecurso.EstadoAsignacion;

@Repository
public interface AsignacionRecursoRepository extends JpaRepository<AsignacionRecurso, Long> {

    List<AsignacionRecurso> findByReporteNecesidadIdOrderByFechaAsignacionDesc(Long reporteId);

    Optional<AsignacionRecurso> findByNumeroSeguimientoEnvio(String numeroSeguimiento);

    List<AsignacionRecurso> findByEnvioIdLogistica(Long envioId);

    long countByEstado(EstadoAsignacion estado);
}
