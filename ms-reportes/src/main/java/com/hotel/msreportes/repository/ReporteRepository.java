package com.hotel.msreportes.repository;

import com.hotel.msreportes.model.Reporte;
import com.hotel.msreportes.model.TiposReporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReporteRepository extends JpaRepository<Reporte, Long> {

    // Buscar por tipo de reporte
    List<Reporte> findByTipo(TiposReporte tipo);

    // Buscar por hotel
    List<Reporte> findByHotelId(Long hotelId);

    // Buscar reportes cuya fecha de inicio está dentro de un rango
    List<Reporte> findByFechaInicioBetween(LocalDate inicio, LocalDate fin);

    // Buscar por tipo Y hotel
    List<Reporte> findByTipoAndHotelId(TiposReporte tipo, Long hotelId);

    // Calcular ingreso total de un hotel en un periodo (regla de negocio)
    @Query("SELECT SUM(r.totalIngresos) FROM Reporte r " +
            "WHERE r.hotelId = :hotelId " +
            "AND r.fechaInicio >= :inicio AND r.fechaFin <= :fin")
    Double calcularIngresosTotales(@Param("hotelId") Long hotelId,
                                   @Param("inicio") LocalDate inicio,
                                   @Param("fin") LocalDate fin);
}