package com.hotel.mshoteles.service;

import com.hotel.mshoteles.dto.HotelRequestDTO;
import com.hotel.mshoteles.dto.HotelResponseDTO;
import com.hotel.mshoteles.exception.HotelNotFoundException;
import com.hotel.mshoteles.model.Hotel;
import com.hotel.mshoteles.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HotelService {

    private final HotelRepository hotelRepository;

    public List<HotelResponseDTO> obtenerTodos() {
        log.info("Obteniendo todos los hoteles");

        return hotelRepository.findByActivoTrue()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public HotelResponseDTO obtenerPorId(Long id) {
        log.info("Buscando hotel con id: {}", id);

        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Hotel no encontrado con id: {}", id);
                    return new HotelNotFoundException("Hotel no encontrado con id: " + id);
                });

        return convertirADTO(hotel);
    }

    public List<HotelResponseDTO> obtenerPorCiudad(String ciudad) {
        log.info("Buscando hoteles en ciudad: {}", ciudad);

        return hotelRepository.findByCiudadAndActivoTrue(ciudad)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public HotelResponseDTO crear(HotelRequestDTO dto) {

        log.info("Creando nuevo hotel: {}", dto.getNombre());

        if (hotelRepository.existsByCorreo(dto.getCorreo())) {
            throw new IllegalArgumentException(
                    "Ya existe un hotel con el correo: " + dto.getCorreo()
            );
        }

        Hotel hotel = convertirAEntidad(dto);

        Hotel guardado = hotelRepository.save(hotel);

        log.info("Hotel creado con id: {}", guardado.getId());

        return convertirADTO(guardado);
    }

    public HotelResponseDTO actualizar(Long id, HotelRequestDTO dto) {

        log.info("Actualizando hotel con id: {}", id);

        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Hotel no encontrado con id: {}", id);
                    return new HotelNotFoundException("Hotel no encontrado con id: " + id);
                });

        hotel.setNombre(dto.getNombre());
        hotel.setDireccion(dto.getDireccion());
        hotel.setCiudad(dto.getCiudad());
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
                    log.error("Hotel no encontrado con id: {}", id);
                    return new HotelNotFoundException("Hotel no encontrado con id: " + id);
                });

        hotel.setActivo(false);

        hotelRepository.save(hotel);

        log.info("Hotel desactivado correctamente con id: {}", id);
    }

    private HotelResponseDTO convertirADTO(Hotel hotel) {

        HotelResponseDTO dto = new HotelResponseDTO();

        dto.setId(hotel.getId());
        dto.setNombre(hotel.getNombre());
        dto.setDireccion(hotel.getDireccion());
        dto.setCiudad(hotel.getCiudad());
        dto.setPais(hotel.getPais());
        dto.setEstrellas(hotel.getEstrellas());
        dto.setTelefono(hotel.getTelefono());
        dto.setCorreo(hotel.getCorreo());
        dto.setActivo(hotel.isActivo());

        return dto;
    }

    private Hotel convertirAEntidad(HotelRequestDTO dto) {

        Hotel hotel = new Hotel();

        hotel.setNombre(dto.getNombre());
        hotel.setDireccion(dto.getDireccion());
        hotel.setCiudad(dto.getCiudad());
        hotel.setPais(dto.getPais());
        hotel.setEstrellas(dto.getEstrellas());
        hotel.setTelefono(dto.getTelefono());
        hotel.setCorreo(dto.getCorreo());
        hotel.setActivo(true);

        return hotel;
    }
}
