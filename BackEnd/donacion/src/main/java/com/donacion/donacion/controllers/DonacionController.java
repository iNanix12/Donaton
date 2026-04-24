package com.donacion.donacion.controllers;

import com.donacion.donacion.dtos.DonacionRequestDTO;
import com.donacion.donacion.dtos.DonacionResponseDTO;
import com.donacion.donacion.entities.RecursoDonado;
import com.donacion.donacion.services.DonacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/donaciones")
@RequiredArgsConstructor
@Slf4j
public class DonacionController {

    private final DonacionService donacionService;

    @PostMapping
    public ResponseEntity<DonacionResponseDTO> registrarDonacion(
            @Valid @RequestBody DonacionRequestDTO dto) {

        log.info("POST /api/donaciones - RUT: {}, tipo: {}", dto.getRut(), dto.getTipoDonante());
        DonacionResponseDTO response = donacionService.procesarDonacion(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

   
    @GetMapping("/historial")
    public ResponseEntity<List<RecursoDonado>> obtenerHistorialDonante(
            @RequestParam String rut) {

        log.info("GET /api/donaciones/historial - RUT: {}", rut);
        List<RecursoDonado> historial = donacionService.obtenerHistorialPorRut(rut);
        return ResponseEntity.ok(historial);
    }

    
    @GetMapping("/historial/paginado")
    public ResponseEntity<Page<RecursoDonado>> obtenerHistorialPaginado(
            @RequestParam String rut,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("fechaDonacion").descending());
        Page<RecursoDonado> resultado = donacionService.obtenerHistorialPorRutPaginado(rut, pageable);
        return ResponseEntity.ok(resultado);
    }

    
    @GetMapping("/admin")
    public ResponseEntity<Page<RecursoDonado>> obtenerHistorialGlobal(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.info("GET /api/donaciones/admin - página: {}", page);
        Pageable pageable = PageRequest.of(page, size, Sort.by("fechaDonacion").descending());
        Page<RecursoDonado> resultado = donacionService.obtenerHistorialGlobal(pageable);
        return ResponseEntity.ok(resultado);
    }
}