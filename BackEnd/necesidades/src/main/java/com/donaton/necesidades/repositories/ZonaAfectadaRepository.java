package com.donaton.necesidades.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.donaton.necesidades.entities.ZonaAfectada;
import com.donaton.necesidades.entities.ZonaAfectada.EstadoZona;
import com.donaton.necesidades.entities.ZonaAfectada.TipoEmergencia;

@Repository
public interface ZonaAfectadaRepository extends JpaRepository<ZonaAfectada, Long> {

    List<ZonaAfectada> findByEstadoOrderByFechaAperturaDesc(EstadoZona estado);

    List<ZonaAfectada> findByRegionIgnoreCaseAndEstadoOrderByFechaAperturaDesc(
        String region, EstadoZona estado);

    List<ZonaAfectada> findByTipoEmergenciaAndEstadoOrderByFechaAperturaDesc(
        TipoEmergencia tipo, EstadoZona estado);

        //Carga la zona con sus reportes para evitar n + 1 al mostrar el panel

        @Query("SELECT z FROM ZonaAfectada z LEFT JOIN FETCH z.reportes WHERE z.id = :id")
        Optional<ZonaAfectada> findByIdWithReportes(@Param("id") Long id);

        long countByEstado(EstadoZona estado);

}
