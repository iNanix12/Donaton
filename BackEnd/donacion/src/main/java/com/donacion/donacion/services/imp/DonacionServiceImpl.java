package com.donacion.donacion.services.imp;


import com.donacion.donacion.dtos.DonacionRequestDTO;
import com.donacion.donacion.dtos.DonacionResponseDTO;
import com.donacion.donacion.entities.*;
import com.donacion.donacion.repositories.DonanteRepository;
import com.donacion.donacion.repositories.RecursoDonadoRepository;
import com.donacion.donacion.services.DonacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DonacionServiceImpl implements DonacionService {

    private final DonanteRepository donanteRepository;
    private final RecursoDonadoRepository recursoDonadoRepository;

    
    @Override
    @Transactional
    public DonacionResponseDTO procesarDonacion(DonacionRequestDTO dto) {
        String rutNormalizado = normalizarRut(dto.getRut());
        log.info("Procesando donación para RUT: {}", rutNormalizado);

        // Paso 1: buscar o crear donante
        boolean donanteNuevo = !donanteRepository.existsByRut(rutNormalizado);
        Donante donante = donanteRepository.findByRut(rutNormalizado)
                .orElseGet(() -> crearDonante(dto, rutNormalizado));

        // Paso 2: construir el recurso donado
        RecursoDonado recurso = construirRecursoDonado(dto, donante);
        recursoDonadoRepository.save(recurso);

        log.info("Donación registrada: recursoId={}, donanteId={}, tipo={}",
                recurso.getId(), donante.getId(), recurso.getTipoRecurso());

        return DonacionResponseDTO.builder()
                .recursoId(recurso.getId())
                .donanteId(donante.getId())
                .rutDonante(donante.getRut())
                .nombreDonante(donante.getNombreVisible())
                .tipoDonante(dto.getTipoDonante())
                .donanteNuevo(donanteNuevo)
                .tipoRecurso(recurso.getTipoRecurso())
                .cantidad(recurso.getCantidad())
                .unidadMedida(recurso.getUnidadMedida())
                .descripcion(recurso.getDescripcion())
                .centroAcopioId(recurso.getCentroAcopioId())
                .fechaDonacion(recurso.getFechaDonacion())
                .mensaje(donanteNuevo
                        ? "Donante registrado y donación procesada exitosamente."
                        : "Donación procesada exitosamente para donante existente.")
                .build();
    }

    // ── Historial ────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<RecursoDonado> obtenerHistorialPorRut(String rut) {
        String rutNormalizado = normalizarRut(rut);
        log.debug("Consultando historial para RUT: {}", rutNormalizado);
        return recursoDonadoRepository.findHistorialByRut(rutNormalizado);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RecursoDonado> obtenerHistorialPorRutPaginado(String rut, Pageable pageable) {
        String rutNormalizado = normalizarRut(rut);
        return recursoDonadoRepository.findHistorialByRutPaginado(rutNormalizado, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RecursoDonado> obtenerHistorialGlobal(Pageable pageable) {
        log.debug("Consultando historial global - página: {}", pageable.getPageNumber());
        return recursoDonadoRepository.findAllConDonante(pageable);
    }

    // ── Métodos privados de apoyo ─────────────────────────────────────────

    private Donante crearDonante(DonacionRequestDTO dto, String rutNormalizado) {
        log.info("Creando nuevo donante de tipo: {} con RUT: {}", dto.getTipoDonante(), rutNormalizado);

        Donante donante = switch (dto.getTipoDonante().toUpperCase()) {
            case "PARTICULAR" -> {
                validarCamposParticular(dto);
                Particular p = new Particular();
                p.setNombres(dto.getNombres());
                p.setApellidos(dto.getApellidos());
                yield p;
            }
            case "EMPRESA" -> {
                validarCamposEmpresa(dto);
                Empresa e = new Empresa();
                e.setRazonSocial(dto.getRazonSocial());
                e.setNombreContacto(dto.getNombreContacto());
                e.setGiro(dto.getGiro());
                yield e;
            }
            default -> throw new IllegalArgumentException(
                    "Tipo de donante no reconocido: " + dto.getTipoDonante());
        };

        // Campos comunes
        donante.setRut(rutNormalizado);
        donante.setEmail(dto.getEmail());
        donante.setTelefono(dto.getTelefono());
        donante.setDireccion(dto.getDireccion());

        return donanteRepository.save(donante);
    }

    /**
     * Construye un RecursoDonado a partir del DTO y el donante resuelto.
     */
    private RecursoDonado construirRecursoDonado(DonacionRequestDTO dto, Donante donante) {
        RecursoDonado recurso = new RecursoDonado();
        recurso.setDonante(donante);
        recurso.setTipoRecurso(dto.getTipoRecurso());
        recurso.setCantidad(dto.getCantidad());
        recurso.setUnidadMedida(dto.getUnidadMedida());
        recurso.setDescripcion(dto.getDescripcion());
        recurso.setCentroAcopioId(dto.getCentroAcopioId());
        recurso.setObservaciones(dto.getObservaciones());
        return recurso;
    }

    /**
     * Normaliza el RUT removiendo puntos para estandarizar la búsqueda.
     * Ejemplo: "12.345.678-9" → "12345678-9"
     */
    private String normalizarRut(String rut) {
        return rut.replace(".", "").trim().toUpperCase();
    }

    private void validarCamposParticular(DonacionRequestDTO dto) {
        if (dto.getNombres() == null || dto.getNombres().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio para donantes de tipo PARTICULAR.");
        }
        if (dto.getApellidos() == null || dto.getApellidos().isBlank()) {
            throw new IllegalArgumentException("Los apellidos son obligatorios para donantes de tipo PARTICULAR.");
        }
    }

    private void validarCamposEmpresa(DonacionRequestDTO dto) {
        if (dto.getRazonSocial() == null || dto.getRazonSocial().isBlank()) {
            throw new IllegalArgumentException("La razón social es obligatoria para donantes de tipo EMPRESA.");
        }
        if (dto.getNombreContacto() == null || dto.getNombreContacto().isBlank()) {
            throw new IllegalArgumentException("El nombre de contacto es obligatorio para donantes de tipo EMPRESA.");
        }
    }
}