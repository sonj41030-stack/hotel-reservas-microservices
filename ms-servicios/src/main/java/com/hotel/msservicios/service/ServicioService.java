package com.hotel.msservicios.service;

import com.hotel.msservicios.dto.ServicioRequestDTO;
import com.hotel.msservicios.dto.ServicioResponseDTO;
import com.hotel.msservicios.exception.ServicioNotFoundException;
import com.hotel.msservicios.model.Servicio;
import com.hotel.msservicios.repository.ServicioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServicioService {

    private final ServicioRepository servicioRepository;

    public List<ServicioResponseDTO> obtenerTodos() {
        log.info("Obteniendo todos los servicios");

        return servicioRepository.findByActivoTrue()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public ServicioResponseDTO obtenerPorId(Long id) {
        log.info("Buscando servicio con id: {}", id);

        Servicio servicio = servicioRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Servicio no encontrado con id: {}", id);
                    return new ServicioNotFoundException("Servicio no encontrado con id: " + id);
                });

        return convertirADTO(servicio);
    }

    public List<ServicioResponseDTO> obtenerPorTipo(String tipo) {
        log.info("Buscando servicios por tipo: {}", tipo);

        return servicioRepository.findByTipoAndActivoTrue(tipo)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public List<ServicioResponseDTO> obtenerDisponibles() {
        log.info("Buscando servicios disponibles");

        return servicioRepository.findByDisponibleTrue()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public ServicioResponseDTO crear(ServicioRequestDTO dto) {
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

    public ServicioResponseDTO actualizar(Long id, ServicioRequestDTO dto) {
        log.info("Actualizando servicio con id: {}", id);

        Servicio servicio = servicioRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Servicio no encontrado para actualizar, id: {}", id);
                    return new ServicioNotFoundException("Servicio no encontrado con id: " + id);
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
                    return new ServicioNotFoundException("Servicio no encontrado con id: " + id);
                });

        servicio.setActivo(false);
        servicioRepository.save(servicio);

        log.info("Servicio desactivado correctamente con id: {}", id);
    }

    private ServicioResponseDTO convertirADTO(Servicio servicio) {
        ServicioResponseDTO dto = new ServicioResponseDTO();

        dto.setId(servicio.getId());
        dto.setNombre(servicio.getNombre());
        dto.setDescripcion(servicio.getDescripcion());
        dto.setPrecio(servicio.getPrecio());
        dto.setTipo(servicio.getTipo());
        dto.setDisponible(servicio.isDisponible());
        dto.setActivo(servicio.isActivo());

        return dto;
    }

    private Servicio convertirAEntidad(ServicioRequestDTO dto) {
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