package com.hotel.mshabitaciones.service;

import com.hotel.mshabitaciones.dto.HabitacionRequestDTO;
import com.hotel.mshabitaciones.dto.HabitacionResponseDTO;
import com.hotel.mshabitaciones.exception.HabitacionNotFoundException;
import com.hotel.mshabitaciones.model.Habitacion;
import com.hotel.mshabitaciones.repository.HabitacionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HabitacionService {

    private final HabitacionRepository habitacionRepository;

    public List<HabitacionResponseDTO> obtenerTodos() {
        log.info("Obteniendo todas las habitaciones");

        return habitacionRepository.findByActivoTrue()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public HabitacionResponseDTO obtenerPorId(Long id) {
        log.info("Buscando habitacion con id: {}", id);

        Habitacion habitacion = habitacionRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Habitacion no encontrada con id: {}", id);
                    return new HabitacionNotFoundException(
                            "Habitacion no encontrada con id: " + id
                    );
                });

        return convertirADTO(habitacion);
    }

    public List<HabitacionResponseDTO> obtenerPorHotel(Long hotelId) {
        log.info("Buscando habitaciones del hotel id: {}", hotelId);

        return habitacionRepository.findByHotelIdAndActivoTrue(hotelId)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public List<HabitacionResponseDTO> obtenerDisponibles() {
        log.info("Buscando habitaciones disponibles");

        return habitacionRepository.findByDisponibleTrue()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public List<HabitacionResponseDTO> obtenerPermiteMascotas() {
        log.info("Buscando habitaciones que permiten mascotas");

        return habitacionRepository.findByPermiteMascotasTrue()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public HabitacionResponseDTO crear(HabitacionRequestDTO dto) {
        log.info("Creando nueva habitacion tipo: {}", dto.getTipo());

        Habitacion habitacion = convertirAEntidad(dto);

        Habitacion guardada = habitacionRepository.save(habitacion);

        log.info("Habitacion creada con id: {}", guardada.getId());

        return convertirADTO(guardada);
    }

    public HabitacionResponseDTO actualizar(Long id, HabitacionRequestDTO dto) {
        log.info("Actualizando habitacion con id: {}", id);

        Habitacion habitacion = habitacionRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Habitacion no encontrada para actualizar, id: {}", id);
                    return new HabitacionNotFoundException(
                            "Habitacion no encontrada con id: " + id
                    );
                });

        habitacion.setHotelId(dto.getHotelId());
        habitacion.setTipo(dto.getTipo());
        habitacion.setCapacidad(dto.getCapacidad());
        habitacion.setPrecio(dto.getPrecio());
        habitacion.setDisponible(dto.isDisponible());
        habitacion.setPermiteMascotas(dto.isPermiteMascotas());

        Habitacion actualizada = habitacionRepository.save(habitacion);

        log.info("Habitacion actualizada correctamente con id: {}", actualizada.getId());

        return convertirADTO(actualizada);
    }

    public void eliminar(Long id) {
        log.info("Eliminando habitacion con id: {}", id);

        Habitacion habitacion = habitacionRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Habitacion no encontrada para eliminar, id: {}", id);
                    return new HabitacionNotFoundException(
                            "Habitacion no encontrada con id: " + id
                    );
                });

        habitacion.setActivo(false);
        habitacionRepository.save(habitacion);

        log.info("Habitacion desactivada correctamente con id: {}", id);
    }

    private HabitacionResponseDTO convertirADTO(Habitacion habitacion) {
        HabitacionResponseDTO dto = new HabitacionResponseDTO();

        dto.setId(habitacion.getId());
        dto.setHotelId(habitacion.getHotelId());
        dto.setTipo(habitacion.getTipo());
        dto.setCapacidad(habitacion.getCapacidad());
        dto.setPrecio(habitacion.getPrecio());
        dto.setDisponible(habitacion.isDisponible());
        dto.setPermiteMascotas(habitacion.isPermiteMascotas());
        dto.setActivo(habitacion.isActivo());

        return dto;
    }

    private Habitacion convertirAEntidad(HabitacionRequestDTO dto) {
        Habitacion habitacion = new Habitacion();

        habitacion.setHotelId(dto.getHotelId());
        habitacion.setTipo(dto.getTipo());
        habitacion.setCapacidad(dto.getCapacidad());
        habitacion.setPrecio(dto.getPrecio());
        habitacion.setDisponible(dto.isDisponible());
        habitacion.setPermiteMascotas(dto.isPermiteMascotas());
        habitacion.setActivo(true);

        return habitacion;
    }
}
