package com.logistica.logistica.services;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.logistica.logistica.dtos.LogisticaDTO.ActualizarEstadoRequest;
import com.logistica.logistica.dtos.LogisticaDTO.CentroAcopioRequest;
import com.logistica.logistica.dtos.LogisticaDTO.CentroAcopioResponse;
import com.logistica.logistica.dtos.LogisticaDTO.EnvioRequest;
import com.logistica.logistica.dtos.LogisticaDTO.EnvioResponse;

public interface LogisticaService {


    //-----------------------Centros de Acopio------------------------
    
    CentroAcopioResponse crearCentroAcopio(CentroAcopioRequest request);
    CentroAcopioResponse obtenerCentroPorId(Long id);
    List<CentroAcopioResponse> listarCentrosActivos();
    List<CentroAcopioResponse> listarCentrosPorRegion(String region);

    //Agregamos stock al inventario de un centro cuando llega una donación proveniente del microservicio de donaciones
    void recibirDonacion(Long centroId, String tipoRecurso, BigDecimal cantidad, String unidadMedida);



    //-----------------------Envíos------------------------
    
    EnvioResponse planificarEnvio (EnvioRequest request);
    
    EnvioResponse despacharEnvio(Long envioId);

    EnvioResponse confirmarEntrega (Long envioId, ActualizarEstadoRequest request);

    EnvioResponse cancelarEnvio(Long envioId, ActualizarEstadoRequest request);

    EnvioResponse buscarPorNumeroSeguimiento(String numeroSeguimiento);

    Page<EnvioResponse> listarEnviosPorCentro(Long centroId, Pageable pageable);

    Page<EnvioResponse> listarTodosLosEnvios(Pageable pageable);

    

}
