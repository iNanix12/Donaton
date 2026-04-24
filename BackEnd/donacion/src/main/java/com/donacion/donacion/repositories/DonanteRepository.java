package com.donacion.donacion.repositories;

import com.donacion.donacion.entities.Donante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DonanteRepository extends JpaRepository<Donante, Long> {

    Optional<Donante> findByRut(String rut);

    boolean existsByRut(String rut);

    @Query("SELECT d FROM Donante d LEFT JOIN FETCH d.recursos WHERE d.rut = :rut")
    Optional<Donante> findByRutWithRecursos(@Param("rut") String rut);
}

