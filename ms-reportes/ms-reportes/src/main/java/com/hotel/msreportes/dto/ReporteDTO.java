package com.hotel.msreportes.dto;

import com.hotel.msreportes.model.TiposReporte;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;

public class ReporteDTO {

    @NotNull(message = "El tipo de reporte es obligatorio")
    private TiposReporte tipo;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDate fechaFin;

    @PositiveOrZero(message = "totalReservas no puede ser negativo")
    private Integer totalReservas;

    @PositiveOrZero(message = "totalIngresos no puede ser negativo")
    private Double totalIngresos;

    private Double porcentajeOcupacion;

    private Long hotelId;

    private String observaciones;

    // ── Getters y Setters ──────────────────────────────────────────────────────
    public TiposReporte getTipo() { return tipo; }
    public void setTipo(TiposReporte tipo) { this.tipo = tipo; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public Integer getTotalReservas() { return totalReservas; }
    public void setTotalReservas(Integer totalReservas) { this.totalReservas = totalReservas; }

    public Double getTotalIngresos() { return totalIngresos; }
    public void setTotalIngresos(Double totalIngresos) { this.totalIngresos = totalIngresos; }

    public Double getPorcentajeOcupacion() { return porcentajeOcupacion; }
    public void setPorcentajeOcupacion(Double porcentajeOcupacion) { this.porcentajeOcupacion = porcentajeOcupacion; }

    public Long getHotelId() { return hotelId; }
    public void setHotelId(Long hotelId) { this.hotelId = hotelId; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}