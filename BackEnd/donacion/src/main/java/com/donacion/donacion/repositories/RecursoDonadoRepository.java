package com.donacion.donacion.repositories;

import com.donacion.donacion.entities.RecursoDonado;
import com.donacion.donacion.entities.RecursoDonado.TipoRecurso;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RecursoDonadoRepository extends JpaRepository<RecursoDonado, Long> {

    @Query("SELECT r FROM RecursoDonado r JOIN r.donante d WHERE d.rut = :rut ORDER BY r.fechaDonacion DESC")
    List<RecursoDonado> findHistorialByRut(@Param("rut") String rut);

    @Query("SELECT r FROM RecursoDonado r JOIN r.donante d WHERE d.rut = :rut ORDER BY r.fechaDonacion DESC")
    Page<RecursoDonado> findHistorialByRutPaginado(@Param("rut") String rut, Pageable pageable);

    // ── Panel Administrador ──────────────────────────────────────────────

    @Query("SELECT r FROM RecursoDonado r JOIN FETCH r.donante ORDER BY r.fechaDonacion DESC")
    Page<RecursoDonado> findAllConDonante(Pageable pageable);


    Page<RecursoDonado> findByTipoRecursoOrderByFechaDonacionDesc(
            TipoRecurso tipoRecurso, Pageable pageable);

 
    @Query("SELECT r FROM RecursoDonado r JOIN FETCH r.donante " +
           "WHERE r.fechaDonacion BETWEEN :desde AND :hasta " +
           "ORDER BY r.fechaDonacion DESC")
    Page<RecursoDonado> findByRangoDeFechas(
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta,
            Pageable pageable);

   
    List<RecursoDonado> findByCentroAcopioIdOrderByFechaDonacionDesc(Long centroAcopioId);
}