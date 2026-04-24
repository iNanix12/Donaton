package com.donacion.donacion.services;

import com.donacion.donacion.dtos.DonacionRequestDTO;
import com.donacion.donacion.dtos.DonacionResponseDTO;
import com.donacion.donacion.entities.RecursoDonado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface DonacionService {

  
    DonacionResponseDTO procesarDonacion(DonacionRequestDTO dto);


    List<RecursoDonado> obtenerHistorialPorRut(String rut);

   
    Page<RecursoDonado> obtenerHistorialPorRutPaginado(String rut, Pageable pageable);

   
    Page<RecursoDonado> obtenerHistorialGlobal(Pageable pageable);
}