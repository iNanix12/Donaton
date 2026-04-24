package com.logistica.logistica.services;

import java.math.BigDecimal;
import java.util.List;

import com.logistica.logistica.dtos.LogisticaDTO.CentroAcopioRequest;
import com.logistica.logistica.dtos.LogisticaDTO.CentroAcopioResponse;

public interface LogisticaService {


    //-----------------------Centros de Acopio------------------------
    
    CentroAcopioResponse crearCentroAcopio(CentroAcopioRequest request);
    CentroAcopioResponse obtenerCentroPorId(Long id);
    List<CentroAcopioResponse> listarCentrosActivos();
    List<CentroAcopioResponse> listarCentrosPorRegion(String region);

    //Agregamos stock al inventario de un centro cuando llega una donación proveniente del microservicio de donaciones
    void recibirDonacion(Long centroId, String tipoRecurso, BigDecimal cantidad, String unidadMedida);



    //-----------------------Envíos------------------------

    

}
