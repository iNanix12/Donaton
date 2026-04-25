package com.logistica.logistica.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.logistica.logistica.entities.Envio;
import com.logistica.logistica.entities.Envio.EstadoEnvio;

@Repository
public interface EnvioRepository extends JpaRepository<Envio, Long> {
 
    Optional<Envio> findByNumeroSeguimiento(String numeroSeguimiento);
 
    Page<Envio> findByCentroOrigenIdOrderByFechaCreacionDesc(Long centroOrigenId, Pageable pageable);
 
    Page<Envio> findByEstadoOrderByFechaCreacionDesc(EstadoEnvio estado, Pageable pageable);
 
    Page<Envio> findAllByOrderByFechaCreacionDesc(Pageable pageable);
 
    @Query("SELECT e FROM Envio e JOIN FETCH e.detalle WHERE e.id = :id")
    Optional<Envio> findByIdWithDetalle(@Param("id") Long id);
 
    long countByEstado(EstadoEnvio estado);
}