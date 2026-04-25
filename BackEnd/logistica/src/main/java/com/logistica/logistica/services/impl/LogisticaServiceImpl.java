package com.logistica.logistica.services.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logistica.logistica.dtos.LogisticaDTO.ActualizarEstadoRequest;
import com.logistica.logistica.dtos.LogisticaDTO.CentroAcopioRequest;
import com.logistica.logistica.dtos.LogisticaDTO.CentroAcopioResponse;
import com.logistica.logistica.dtos.LogisticaDTO.DetalleRequest;
import com.logistica.logistica.dtos.LogisticaDTO.EnvioRequest;
import com.logistica.logistica.dtos.LogisticaDTO.EnvioResponse;
import com.logistica.logistica.dtos.LogisticaDTO.InventarioResponse;
import com.logistica.logistica.entities.CentroAcopio;
import com.logistica.logistica.entities.DetalleEnvio;
import com.logistica.logistica.entities.Envio;
import com.logistica.logistica.entities.ItemInventario;
import com.logistica.logistica.entities.ItemInventario.TipoRecurso;
import com.logistica.logistica.repositories.CentroAcopioRepository;
import com.logistica.logistica.repositories.EnvioRepository;
import com.logistica.logistica.repositories.ItemInventarioRepository;
import com.logistica.logistica.services.LogisticaService;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogisticaServiceImpl implements LogisticaService{

    private final CentroAcopioRepository centroRepo;
    private final ItemInventarioRepository inventarioRepo;
    private final EnvioRepository envioRepo;


    //------------------------Centro de Acopio------------------------

    @Override
    @Transactional
    public CentroAcopioResponse crearCentroAcopio(CentroAcopioRequest request){
        CentroAcopio centro = new CentroAcopio();
        centro.setNombre(request.getNombre());
        centro.setDireccion(request.getDireccion());
        centro.setRegion(request.getRegion());
        centro.setComuna(request.getComuna());
        centro.setLatitud(request.getLatitud());
        centro.setLongitud(request.getLongitud());
        centro.setTelefono(request.getTelefono());
        centro.setEmailEncargado(request.getEmailEncargado());
        centro.setNombreEncargado(request.getNombreEncargado());
        centro.setCapacidadMaximaKg(request.getCapacidadMaximaKg());

        CentroAcopio guardado = centroRepo.save(centro);
        log.info("Centro de acopio creado; id={}, nombre={}", guardado.getId(), guardado.getNombre());
        return mapearCentro(guardado);
    }


    @Override
    @Transactional(readOnly = true)
    public CentroAcopioResponse obtenerCentroPorId(Long id){
        CentroAcopio centro = centroRepo.findByIdWithInventario(id)
        .orElseThrow(() -> new IllegalArgumentException("Centro de acopio no encontrado: "+ id));
        return mapearCentro(centro);
    }


    @Override
    @Transactional(readOnly = true)
    public List<CentroAcopioResponse> listarCentrosActivos() {
    return centroRepo.findByEstadoOrderByNombre(com.logistica.logistica.entities.CentroAcopio.EstadoCentro.ACTIVO)
            .stream()
            .map(this::mapearCentro)
            .collect(Collectors.toList());
}


    @Override
    @Transactional(readOnly = true)
    public List<CentroAcopioResponse> listarCentrosPorRegion(String region){
        return centroRepo.findByRegionIgnoreCaseOrderByNombre(region)
        .stream().map(this::mapearCentro).collect(Collectors.toList());
    }




    //-----------------------------Inventario--------------------------------

    //Recibe una donacion del microservicio Donacion, actualizamos inventario, creamos item que no existe y sumamos existentes

    @Override
    @Transactional
    public void recibirDonacion(Long centroId, String tipoRecursoStr, BigDecimal cantidad, String unidadMedida){
        CentroAcopio centro = centroRepo.findById(centroId)
        .orElseThrow(()-> new IllegalArgumentException("Centro no encontrado: " + centroId));

        TipoRecurso tipoRecurso = TipoRecurso.valueOf(tipoRecursoStr.toUpperCase());

        ItemInventario item = inventarioRepo
            .findByCentroAcopioIdAndTipoRecurso(centroId, tipoRecurso)
            .orElseGet(() -> {
                ItemInventario nuevo = new ItemInventario();
                nuevo.setCentroAcopio(centro);
                nuevo.setTipoRecurso(tipoRecurso);
                nuevo.setUnidadMedida(unidadMedida);
                return nuevo;
            });

            item.agregarStock(cantidad);
            inventarioRepo.save(item);

            log.info("Inventario actualizado: centro={}, tipo={}, +{} {}",
                centroId, tipoRecurso, cantidad, unidadMedida);           

    }




    //----------------------------Envíos--------------------------------------
    //Planificamos un nuevo envío
    //-1 Calida que el centro de origen existe
    //-2 Verificamos Stock suficiente para cada recurso
    //-3 Descuenta el stock del inventario
    //-4 Genera número de seguimiento único
    //-5 Persiste el envío con sus detalles

    @Override
    @Transactional
    public EnvioResponse planificarEnvio(EnvioRequest request) {
        CentroAcopio centroOrigen = centroRepo.findByIdWithInventario(request.getCentroOrigenId())
            .orElseThrow(() -> new IllegalArgumentException("Centro de origen no encontrado: " + request.getCentroOrigenId()));

            //2
            for (DetalleRequest detalle : request.getDetalles()){
                ItemInventario item = inventarioRepo
                .findByCentroAcopioIdAndTipoRecurso(centroOrigen.getId(), detalle.getTipoRecurso())
                .orElseThrow(() -> new IllegalStateException("No hay stock de " + detalle.getTipoRecurso() + "en el centro " + centroOrigen.getNombre()));

                item.descontarStock(detalle.getCantidad());
                inventarioRepo.save(item);
            }

            //Construir la entidad Envio
            Envio envio = new Envio();
            envio.setNumeroSeguimiento(generarNumeroSeguimiento());
            envio.setCentroOrigen(centroOrigen);
            envio.setDestinoDescripcion(request.getDestinoDescripcion());
            envio.setDestinoDireccion(request.getDestinoDireccion());
            envio.setDestinoRegion(request.getDestinoRegion());
            envio.setDestinoComuna(request.getDestinoComuna());
            envio.setTransportistaNombre(request.getTransportistaNombre());
            envio.setTransportistaRut(request.getTransportistaRut());
            envio.setPatenteVehiculo(request.getPatenteVehiculo());
            envio.setFechaPlanificada(request.getFechaPlanificada());
            envio.setObservaciones(request.getObservaciones());

            //Agregar detalles
            for (DetalleRequest detalleDTO : request.getDetalles()) {
                DetalleEnvio detalle = new DetalleEnvio();
                detalle.setEnvio(envio);
                detalle.setTipoRecurso(detalleDTO.getTipoRecurso());
                detalle.setCantidad(detalleDTO.getCantidad());
                detalle.setUnidadMedida(detalleDTO.getUnidadMedida());
                detalle.setDescripcion(detalleDTO.getDescripcion());
                envio.getDetalle().add(detalle);
            }

            Envio guardado = envioRepo.save(envio);
            log.info("Envio planificado: id={}, seguimiento={}, origen={}",
                    guardado.getId(), guardado.getNumeroSeguimiento(), centroOrigen.getNombre());
            
            return mapearEnvio(guardado);

    }


    @Override
    @Transactional
    public EnvioResponse despacharEnvio(Long envioId) {
        Envio envio = obtenerEnvioOException(envioId);
        envio.despachar();
        log.info("Envío despachado: id={}, seguimiento={}", envio.getId(), envio.getNumeroSeguimiento());
        return mapearEnvio(envioRepo.save(envio));
    }



    
    @Override
    @Transactional
    public EnvioResponse confirmarEntrega(Long envioId, ActualizarEstadoRequest request) {
        Envio envio = obtenerEnvioOException(envioId);
        envio.confirmarEntrega(request.getObservaciones());
        log.info("Entrega confirmada: id={}, seguimiento={}", envio.getId(), envio.getNumeroSeguimiento());
        return mapearEnvio(envioRepo.save(envio));
    }
 
    @Override
    @Transactional
    public EnvioResponse cancelarEnvio(Long envioId, ActualizarEstadoRequest request) {
        Envio envio = obtenerEnvioOException(envioId);
 
        // Devolver el stock al inventario al cancelar
        for (DetalleEnvio detalle : envio.getDetalle()) {
            inventarioRepo.findByCentroAcopioIdAndTipoRecurso(
                    envio.getCentroOrigen().getId(), detalle.getTipoRecurso())
                    .ifPresent(item -> {
                        item.agregarStock(detalle.getCantidad());
                        inventarioRepo.save(item);
                    });
        }
 
        envio.cancelar(request.getObservaciones());
        log.info("Envío cancelado: id={}, motivo={}", envio.getId(), request.getObservaciones());
        return mapearEnvio(envioRepo.save(envio));
    }
 
    @Override
    @Transactional(readOnly = true)
    public EnvioResponse buscarPorNumeroSeguimiento(String numeroSeguimiento) {
        Envio envio = envioRepo.findByNumeroSeguimiento(numeroSeguimiento)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Envío no encontrado con seguimiento: " + numeroSeguimiento));
        return mapearEnvio(envio);
    }
 
    @Override
    @Transactional(readOnly = true)
    public Page<EnvioResponse> listarEnviosPorCentro(Long centroId, Pageable pageable) {
        return envioRepo.findByCentroOrigenIdOrderByFechaCreacionDesc(centroId, pageable)
                .map(this::mapearEnvio);
    }
 
    @Override
    @Transactional(readOnly = true)
    public Page<EnvioResponse> listarTodosLosEnvios(Pageable pageable) {
        return envioRepo.findAllByOrderByFechaCreacionDesc(pageable)
                .map(this::mapearEnvio);
    }


 
    // ── Helpers privados ───────────────────────────────────────────────────
 
    private Envio obtenerEnvioOException(Long id) {
        return envioRepo.findByIdWithDetalle(id)
                .orElseThrow(() -> new IllegalArgumentException("Envío no encontrado: " + id));
    }
 
    
    //Genera un número de seguimiento con formato
    //Ejemplo: DON-A3F92C01
     
    private String generarNumeroSeguimiento() {
        return "DON-" + UUID.randomUUID().toString()
                .replace("-", "")
                .substring(0, 8)
                .toUpperCase();
    }
 
    private CentroAcopioResponse mapearCentro(CentroAcopio c) {
        List<InventarioResponse> inventario = c.getInventario().stream()
                .map((ItemInventario i) -> InventarioResponse.builder()
                        .id(i.getId())
                        .tipoRecurso(i.getTipoRecurso())
                        .cantidadDisponible(i.getCantidadDisponible())
                        .unidadMedida(i.getUnidadMedida())
                        .ultimaActualizacion(i.getUltimaActualizacion())
                        .build())
                .collect(Collectors.toList());
 
        return CentroAcopioResponse.builder()
                .id(c.getId())
                .nombre(c.getNombre())
                .direccion(c.getDireccion())
                .region(c.getRegion())
                .comuna(c.getComuna())
                .latitud(c.getLatitud())
                .longitud(c.getLongitud())
                .estado(c.getEstado().name())
                .capacidadMaximaKg(c.getCapacidadMaximaKg())
                .inventario(inventario)
                .build();
    }
 
    private EnvioResponse mapearEnvio(Envio e) {
        return EnvioResponse.builder()
                .id(e.getId())
                .numeroSeguimiento(e.getNumeroSeguimiento())
                .centroOrigenId(e.getCentroOrigen().getId())
                .centroOrigenNombre(e.getCentroOrigen().getNombre())
                .destinoDescripcion(e.getDestinoDescripcion())
                .destinoDireccion(e.getDestinoDireccion())
                .destinoRegion(e.getDestinoRegion())
                .destinoComuna(e.getDestinoComuna())
                .transportistaNombre(e.getTransportistaNombre())
                .patenteVehiculo(e.getPatenteVehiculo())
                .estado(e.getEstado().name())
                .fechaPlanificada(e.getFechaPlanificada())
                .fechaDespacho(e.getFechaDespacho())
                .fechaEntrega(e.getFechaEntrega())
                .observaciones(e.getObservaciones())
                .fechaCreacion(e.getFechaCreacion())
                .build();
    }


}
