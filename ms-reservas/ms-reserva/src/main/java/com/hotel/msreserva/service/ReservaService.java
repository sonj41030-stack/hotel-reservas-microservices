package com.hotel.msreserva.service;
import com.hotel.msreserva.model.Reserva;
import com.hotel.msreserva.dto.ReservaRequest;
import com.hotel.msreserva.model.EstadoReserva;
import com.hotel.msreserva.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import com.hotel.msreserva.dto.ClienteDTO;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class ReservaService {


    private final ReservaRepository reservaRepository;
    private final WebClient.Builder webClientBuilder;

    public Reserva crearReserva(ReservaRequest request) {

        /* VALIDAR CLIENTE EN ms-clientes
        ClienteDTO cliente = webClientBuilder.build()
                .get()
                .uri("http://ms-clientes/clientes/" + request.getClienteId()) // <--- AQUÍ
                .retrieve()
                .bodyToMono(ClienteDTO.class)
                .block();

        // SI EL CLIENTE NO EXISTE
        if (cliente == null) {
            throw new RuntimeException("Cliente no encontrado");
        }

        */// CREAR RESERVA
        Reserva reserva = new Reserva();

        reserva.setClienteId(request.getClienteId());
        reserva.setHabitacionId(request.getHabitacionId());
        reserva.setFechaEntrada(request.getFechaEntrada());
        reserva.setFechaSalida(request.getFechaSalida());
        reserva.setTotal(request.getTotal());

        reserva.setEstado(EstadoReserva.PENDIENTE);

        return reservaRepository.save(reserva);
    }

    public List<Reserva> listarReservas() {
        return reservaRepository.findAll();
    }

    public Optional<Reserva> obtenerReserva(Long id) {
        return reservaRepository.findById(id);
    }

    public List<Reserva> obtenerReservasPorCliente(Long clienteId) {
        return reservaRepository.findByClienteId(clienteId);
    }

    public Reserva actualizarEstado(Long id, EstadoReserva estado) {
        Optional<Reserva> reserva = reservaRepository.findById(id);
        if (reserva.isPresent()) {
            reserva.get().setEstado(estado);
            return reservaRepository.save(reserva.get());
        }
        throw new RuntimeException("Reserva no encontrada");
    }

    public void eliminarReserva(Long id) {
        reservaRepository.deleteById(id);
    }
}