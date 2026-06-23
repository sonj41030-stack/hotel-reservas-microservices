package com.hotel.msservicios.repository;

import com.hotel.msservicios.model.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServicioRepository extends JpaRepository<Servicio, Long> {

    List<Servicio> findByActivoTrue();

    List<Servicio> findByTipo(String tipo);

    List<Servicio> findByTipoAndActivoTrue(String tipo);

    List<Servicio> findByDisponibleTrue();

    List<Servicio> findByPrecioLessThanEqual(double precio);

    boolean existsByNombre(String nombre);

}