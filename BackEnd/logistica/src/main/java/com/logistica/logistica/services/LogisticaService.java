package com.logistica.logistica.services;

import com.logistica.logistica.dtos.LogisticaDTO.CentroAcopioRequest;
import com.logistica.logistica.dtos.LogisticaDTO.CentroAcopioResponse;

public interface LogisticaService {

    //Centros de Acopio

    CentroAcopioResponse crearCentroAcopio(CentroAcopioRequest request);

}
