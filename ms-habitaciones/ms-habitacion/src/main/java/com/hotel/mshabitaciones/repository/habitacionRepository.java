package com.hotel.mshabitaciones.repository;

import com.hotel.mshabitaciones.model.Habitacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface habitacionRepository extends JpaRepository<Habitacion, Long> {

    List<Habitacion> findByActivoTrue();

    List<Habitacion> findByHotelId(Long hotelId);

    List<Habitacion> findByHotelIdAndActivoTrue(Long hotelId);

    List<Habitacion> findByDisponibleTrue();

    List<Habitacion> findByTipo(String tipo);

    List<Habitacion> findByHotelIdAndDisponibleTrue(Long hotelId);

    List<Habitacion> findByPermiteMascotasTrue();

    List<Habitacion> findByPrecioLessThanEqual(double precio);

}
