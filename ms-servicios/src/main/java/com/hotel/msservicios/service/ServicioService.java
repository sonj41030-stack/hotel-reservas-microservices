package com.hotel.msservicios.service;


import com.hotel.msservicios.dto.servicioRequestDTO;
import com.hotel.msservicios.dto.servicioResponseDTO;
import com.hotel.msservicios.exception.servicioNotFoundException;
import com.hotel.msservicios.model.Servicio;
import com.hotel.msservicios.repository.servicioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServicioService {

    private final servicioRepository servicioRepository;

    public List<servicioResponseDTO> obtenerTodos() {
        log.info("Obteniendo todos los servicios");
        return servicioRepository.findByActivoTrue()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    public servicioResponseDTO obtenerPorId(Long id) {
        log.info("Buscando servicio con id: {}", id);
        Servicio servicio = servicioRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Servicio no encontrado con id: {}", id);
                    return new servicioNotFoundException("Servicio no encontrado con id: " + id);
                });
        return convertirADTO(servicio);
    }

    public List<servicioResponseDTO> obtenerPorTipo(String tipo) {
        log.info("Buscando servicios por tipo: {}", tipo);
        return servicioRepository.findByTipoAndActivoTrue(tipo)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public List<servicioResponseDTO> obtenerDisponibles() {
        log.info("Buscando servicios disponibles");
        return servicioRepository.findByDisponibleTrue()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public servicioResponseDTO crear(servicioRequestDTO dto) {
        log.info("Creando nuevo servicio: {}", dto.getNombre());
        if (servicioRepository.existsByNombre(dto.getNombre())) {
            log.error("Ya existe un servicio con el nombre: {}", dto.getNombre());
            throw new IllegalArgumentException("Ya existe un servicio con ese nombre");
        }
        Servicio servicio = convertirAEntidad(dto);
        Servicio guardado = servicioRepository.save(servicio);
        log.info("Servicio creado con id: {}", guardado.getId());
        return convertirADTO(guardado);
    }
    public servicioResponseDTO actualizar(Long id, servicioRequestDTO dto) {
        log.info("Actualizando servicio con id: {}", id);
        Servicio servicio = servicioRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Servicio no encontrado para actualizar, id: {}", id);
                    return new servicioNotFoundException("Servicio no encontrado con id: " + id);
                });
        servicio.setNombre(dto.getNombre());
        servicio.setDescripcion(dto.getDescripcion());
        servicio.setPrecio(dto.getPrecio());
        servicio.setTipo(dto.getTipo());
        servicio.setDisponible(dto.isDisponible());
        Servicio actualizado = servicioRepository.save(servicio);
        log.info("Servicio actualizado correctamente con id: {}", actualizado.getId());
        return convertirADTO(actualizado);
    }

    public void eliminar(Long id) {
        log.info("Eliminando servicio con id: {}", id);
        Servicio servicio = servicioRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Servicio no encontrado para eliminar, id: {}", id);
                    return new servicioNotFoundException("Servicio no encontrado con id: " + id);
                });
        servicio.setActivo(false);
        servicioRepository.save(servicio);
        log.info("Servicio desactivado correctamente con id: {}", id);
    }
    private servicioResponseDTO convertirADTO(Servicio servicio) {
        servicioResponseDTO dto = new servicioResponseDTO();
        dto.setId(servicio.getId());
        dto.setNombre(servicio.getNombre());
        dto.setDescripcion(servicio.getDescripcion());
        dto.setPrecio(servicio.getPrecio());
        dto.setTipo(servicio.getTipo());
        dto.setDisponible(servicio.isDisponible());
        dto.setActivo(servicio.isActivo());
        return dto;
    }
    private Servicio convertirAEntidad(servicioRequestDTO dto) {
        Servicio servicio = new Servicio();
        servicio.setNombre(dto.getNombre());
        servicio.setDescripcion(dto.getDescripcion());
        servicio.setPrecio(dto.getPrecio());
        servicio.setTipo(dto.getTipo());
        servicio.setDisponible(dto.isDisponible());
        servicio.setActivo(true);
        return servicio;
    }


}
