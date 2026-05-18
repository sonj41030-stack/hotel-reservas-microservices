package com.hotel.mshoteles.service;

import com.hotel.mshoteles.dto.hotelRequestDTO;
import com.hotel.mshoteles.dto.hotelResponseDTO;
import com.hotel.mshoteles.exception.hotelNotFoundException;
import com.hotel.mshoteles.model.Hotel;
import com.hotel.mshoteles.repository.hotelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service@RequiredArgsConstructor
public class HotelService {

    private final hotelRepository hotelRepository;

    public List<hotelResponseDTO> obtenerTodos(){
        log.info("Obteniendo todos los hoteles");
        return hotelRepository.findByActivoTrue()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
    public hotelResponseDTO obtenerPorId(Long id){
        log.info("Buscando hotel con id: {}", id);
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Hotel no encontrado con id: {}", id);
                    return new hotelNotFoundException("Hotel no encontrado con id: " + id);
                });
        return convertirADTO(hotel);
    }

    public hotelResponseDTO crear(hotelRequestDTO dto){
        log.info("Creando nuevo hotel : {}", dto.getNombre());
        if (hotelRepository.existsByCorreo(dto.getCorreo())){
            log.error("El hotel ya existe: {}", dto.getCorreo());
            throw new IllegalArgumentException("Ya existe un hotel con el correo ingresado");
        }
        Hotel hotel = convertirAEntidad( dto);
        Hotel guardado = hotelRepository.save(hotel);
        log.info("El hotel ha sido creado con id: {}", guardado.getId());
        return convertirADTO(guardado);

    }

    public hotelResponseDTO actualizar(Long id, hotelRequestDTO dto) {
        log.info("Actualizando hotel con id: {}", id);
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Hotel no encontrado para actualizar, id: {}", id);
                    return new hotelNotFoundException("Hotel no encontrado con id: " + id);
                });
        hotel.setNombre(dto.getNombre());
        hotel.setDireccion(dto.getDireccion());
        hotel.setCuidad(dto.getCuidad());
        hotel.setPais(dto.getPais());
        hotel.setEstrellas(dto.getEstrellas());
        hotel.setTelefono(dto.getTelefono());
        hotel.setCorreo(dto.getCorreo());
        Hotel actualizado = hotelRepository.save(hotel);
        log.info("Hotel actualizado correctamente con id: {}", actualizado.getId());
        return convertirADTO(actualizado);
    }

    public void eliminar(Long id) {
        log.info("Eliminando hotel con id: {}", id);
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Hotel no encontrado para eliminar, id: {}", id);
                    return new hotelNotFoundException("Hotel no encontrado con id: " + id);
                });
        hotel.setActivo(false);
        hotelRepository.save(hotel);
        log.info("Hotel desactivado correctamente con id: {}", id);
    }
    public List<hotelResponseDTO> obtenerPorCuidad(String cuidad) {
        log.info("Buscando hoteles en cuidad: {}", cuidad);
        return hotelRepository.findByCuidadAndActivoTrue(cuidad)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    private hotelResponseDTO convertirADTO(Hotel hotel) {
        hotelResponseDTO dto = new hotelResponseDTO();
        dto.setId(hotel.getId());
        dto.setNombre(hotel.getNombre());
        dto.setDireccion(hotel.getDireccion());
        dto.setCuidad(hotel.getCuidad());
        dto.setPais(hotel.getPais());
        dto.setEstrellas(hotel.getEstrellas());
        dto.setTelefono(hotel.getTelefono());
        dto.setCorreo(hotel.getCorreo());
        dto.setActivo(hotel.isActivo());
        return dto;
    }

    private Hotel convertirAEntidad(hotelRequestDTO dto) {
        Hotel hotel = new Hotel();
        hotel.setNombre(dto.getNombre());
        hotel.setDireccion(dto.getDireccion());
        hotel.setCuidad(dto.getCuidad());
        hotel.setPais(dto.getPais());
        hotel.setEstrellas(dto.getEstrellas());
        hotel.setTelefono(dto.getTelefono());
        hotel.setCorreo(dto.getCorreo());
        hotel.setActivo(true);
        return hotel;
    }
}

