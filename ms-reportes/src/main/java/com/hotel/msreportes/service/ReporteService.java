package com.hotel.msreportes.service;

import com.hotel.msreportes.client.HotelClient;
import com.hotel.msreportes.dto.ReporteDTO;
import com.hotel.msreportes.model.Reporte;
import com.hotel.msreportes.model.TiposReporte;
import com.hotel.msreportes.repository.ReporteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReporteService {

    private static final Logger log = LoggerFactory.getLogger(ReporteService.class);

    private final ReporteRepository reporteRepository;
    private final HotelClient hotelClient;

    public ReporteService(ReporteRepository reporteRepository, HotelClient hotelClient) {
        this.reporteRepository = reporteRepository;
        this.hotelClient = hotelClient;
    }

    // ── Listar todos ───────────────────────────────────────────────────────────
    public List<Reporte> listarTodos() {
        log.info("Listando todos los reportes");
        return reporteRepository.findAll();
    }

    // ── Buscar por ID ──────────────────────────────────────────────────────────
    public Reporte obtenerPorId(Long id) {
        log.info("Buscando reporte con id: {}", id);
        return reporteRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Reporte no encontrado con id: {}", id);
                    return new RuntimeException("Reporte no encontrado con id: " + id);
                });
    }

    // ── Generar (crear) un reporte ─────────────────────────────────────────────
    public Reporte generar(ReporteDTO dto) {
        log.info("Generando reporte tipo: {} para hotel: {}", dto.getTipo(), dto.getHotelId());

        // ── Regla de negocio 1: fechaInicio no puede ser posterior a fechaFin ──
        if (dto.getFechaInicio().isAfter(dto.getFechaFin())) {
            log.error("Fecha inicio {} es posterior a fecha fin {}", dto.getFechaInicio(), dto.getFechaFin());
            throw new RuntimeException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }

        // ── Regla de negocio 2: porcentaje de ocupación entre 0 y 100 ──────────
        if (dto.getPorcentajeOcupacion() != null &&
                (dto.getPorcentajeOcupacion() < 0 || dto.getPorcentajeOcupacion() > 100)) {
            log.error("Porcentaje de ocupación inválido: {}", dto.getPorcentajeOcupacion());
            throw new RuntimeException("El porcentaje de ocupación debe estar entre 0 y 100");
        }

        // ── Comunicación con ms-hoteles: validar que el hotel existe ────────────
        if (dto.getHotelId() != null) {
            try {
                hotelClient.obtenerHotel(dto.getHotelId());
                log.info("Hotel {} validado correctamente desde ms-hoteles", dto.getHotelId());
            } catch (Exception e) {
                log.error("Hotel con id {} no encontrado en ms-hoteles: {}", dto.getHotelId(), e.getMessage());
                throw new RuntimeException("El hotel con id " + dto.getHotelId() + " no existe o no está disponible");
            }
        }

        Reporte r = new Reporte();
        r.setTipo(dto.getTipo());
        r.setFechaInicio(dto.getFechaInicio());
        r.setFechaFin(dto.getFechaFin());
        r.setTotalReservas(dto.getTotalReservas() != null ? dto.getTotalReservas() : 0);
        r.setTotalIngresos(dto.getTotalIngresos() != null ? dto.getTotalIngresos() : 0.0);
        r.setPorcentajeOcupacion(dto.getPorcentajeOcupacion() != null ? dto.getPorcentajeOcupacion() : 0.0);
        r.setHotelId(dto.getHotelId());
        r.setObservaciones(dto.getObservaciones());

        Reporte guardado = reporteRepository.save(r);
        log.info("Reporte generado con id: {}", guardado.getId());
        return guardado;
    }

    // ── Actualizar un reporte ──────────────────────────────────────────────────
    public Reporte actualizar(Long id, ReporteDTO dto) {
        log.info("Actualizando reporte con id: {}", id);
        Reporte r = obtenerPorId(id);

        // Reusar validación de fechas
        if (dto.getFechaInicio().isAfter(dto.getFechaFin())) {
            throw new RuntimeException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }

        r.setTipo(dto.getTipo());
        r.setFechaInicio(dto.getFechaInicio());
        r.setFechaFin(dto.getFechaFin());
        r.setTotalReservas(dto.getTotalReservas() != null ? dto.getTotalReservas() : r.getTotalReservas());
        r.setTotalIngresos(dto.getTotalIngresos() != null ? dto.getTotalIngresos() : r.getTotalIngresos());
        r.setPorcentajeOcupacion(dto.getPorcentajeOcupacion() != null ? dto.getPorcentajeOcupacion() : r.getPorcentajeOcupacion());
        r.setObservaciones(dto.getObservaciones());

        Reporte actualizado = reporteRepository.save(r);
        log.info("Reporte {} actualizado correctamente", id);
        return actualizado;
    }

    // ── Eliminar ───────────────────────────────────────────────────────────────
    public void eliminar(Long id) {
        log.info("Eliminando reporte con id: {}", id);
        Reporte r = obtenerPorId(id);
        reporteRepository.delete(r);
        log.info("Reporte {} eliminado correctamente", id);
    }

    // ── Filtros ────────────────────────────────────────────────────────────────
    public List<Reporte> obtenerPorTipo(TiposReporte tipo) {
        log.info("Buscando reportes de tipo: {}", tipo);
        return reporteRepository.findByTipo(tipo);
    }

    public List<Reporte> obtenerPorHotel(Long hotelId) {
        log.info("Buscando reportes del hotel: {}", hotelId);
        return reporteRepository.findByHotelId(hotelId);
    }

    public List<Reporte> obtenerPorRango(LocalDate inicio, LocalDate fin) {
        log.info("Buscando reportes entre {} y {}", inicio, fin);
        if (inicio.isAfter(fin)) {
            throw new RuntimeException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }
        return reporteRepository.findByFechaInicioBetween(inicio, fin);
    }

    // ── Regla de negocio: ingresos totales de un hotel en un periodo ───────────
    public Double calcularIngresosTotales(Long hotelId, LocalDate inicio, LocalDate fin) {
        log.info("Calculando ingresos totales para hotel {} entre {} y {}", hotelId, inicio, fin);
        Double total = reporteRepository.calcularIngresosTotales(hotelId, inicio, fin);
        // Si no hay reportes, retornar 0.0 en lugar de null
        return total != null ? total : 0.0;
    }
}