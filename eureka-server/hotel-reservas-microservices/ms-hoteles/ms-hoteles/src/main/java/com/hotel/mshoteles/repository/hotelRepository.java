package com.hotel.mshoteles.repository;


import com.hotel.mshoteles.model.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface hotelRepository extends JpaRepository<Hotel, Long> {

    List<Hotel> findByActivoTrue();

    List<Hotel> findByCuidad(String cuidad);

    List<Hotel> findByCuidadAndActivoTrue(String cuidad);

    List<Hotel> findByEstrellas(int estrellas);

    boolean existsByCorreo(String correo);

    Optional<Hotel> findByCorreo(String correo);
}
