package com.logistica.logistica.repositories;

import java.util.List;
import java.util.Optional;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.logistica.logistica.entities.CentroAcopio;
import com.logistica.logistica.entities.CentroAcopio.EstadoCentro;





//------------------------CentroAcopioRepository-------------------------
@Repository
public interface CentroAcopioRepository extends JpaRepository<CentroAcopio, Long> {

    List <CentroAcopio> findByEstadoOrderByNombre(EstadoCentro estado);

    List <CentroAcopio> findByRegionIgnoreCaseOrderByNombre (String region);

    @Query("SELECT c FROM CentroAcopio c LEFT JOIN FETCH c.inventario WHERE c.id = :id")
    Optional<CentroAcopio> findByIdWithInventario(@Param("id") Long id);

}






