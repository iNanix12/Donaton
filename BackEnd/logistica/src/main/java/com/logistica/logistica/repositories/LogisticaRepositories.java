package com.logistica.logistica.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.logistica.logistica.entities.CentroAcopio;
import com.logistica.logistica.entities.Envio;
import com.logistica.logistica.entities.ItemInventario;
import com.logistica.logistica.entities.CentroAcopio.EstadoCentro;
import com.logistica.logistica.entities.Envio.EstadoEnvio;
import com.logistica.logistica.entities.ItemInventario.TipoRecurso;




//------------------------CentroAcopioRepository-------------------------
@Repository
interface CentroAcopioRepository extends JpaRepository<CentroAcopio, Long> {

    List <CentroAcopio> findByEstadoOrderByNombre(EstadoCentro estado);

    List <CentroAcopio> findByRegionIgnoreCaseOrderByNombre (String region);

    @Query("SELECT c FROM CentroAcopio c LEFT JOIN FETCH c.inventario WHERE c.id = :id")
    Optional<CentroAcopio> findByIdWhitInventario(@Param("id") Long id);

}


//------------------------ItemInventarioRepository-------------------------

@Repository
interface ItemInventarioRepository extends JpaRepository<ItemInventario, Long> {

    List<ItemInventario> findByCentroAcopioId(Long centroAcopioId);

    Optional<ItemInventario> findByCentroAcopioAndTipoRecurso (Long centroAcopioId, TipoRecurso tipoRecurso);

    //Todos los centros que tienen stock disponible de un tipo de recurso
    @Query("SELECT i FROM ItemInventario i JOIN FETCH i.centroAcopio"+ "WHERE i.tipoRecurso =:tipo AND i.cantidadDisponible > 0")
    List<ItemInventario> findCentrosConStock(@Param("tipo") TipoRecurso tipoRecurso);

}



//------------------------EnvioRepository-------------------------

@Repository
interface EnvioRepository extends JpaRepository<Envio, Long> {
 
    Optional<Envio> findByNumeroSeguimiento(String numeroSeguimiento);
 
    Page<Envio> findByCentroOrigenIdOrderByFechaCreacionDesc(Long centroOrigenId, Pageable pageable);
 
    Page<Envio> findByEstadoOrderByFechaCreacionDesc(EstadoEnvio estado, Pageable pageable);
 
    Page<Envio> findAllByOrderByFechaCreacionDesc(Pageable pageable);
 
    @Query("SELECT e FROM Envio e JOIN FETCH e.detalles WHERE e.id = :id")
    Optional<Envio> findByIdWithDetalles(@Param("id") Long id);
 
    long countByEstado(EstadoEnvio estado);
}