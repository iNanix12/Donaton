package com.logistica.logistica.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.logistica.logistica.entities.ItemInventario;
import com.logistica.logistica.entities.ItemInventario.TipoRecurso;

//------------------------ItemInventarioRepository-------------------------

@Repository
public interface ItemInventarioRepository extends JpaRepository<ItemInventario, Long> {

    List<ItemInventario> findByCentroAcopioId(Long centroAcopioId);

    Optional<ItemInventario> findByCentroAcopioIdAndTipoRecurso (Long centroAcopioId, TipoRecurso tipoRecurso);

    //Todos los centros que tienen stock disponible de un tipo de recurso
    @Query("SELECT i FROM ItemInventario i JOIN FETCH i.centroAcopio " + 
           "WHERE i.tipoRecurso = :tipo AND i.cantidadDisponible > 0")
    List<ItemInventario> findCentrosConStock(@Param("tipo") TipoRecurso tipoRecurso);
}