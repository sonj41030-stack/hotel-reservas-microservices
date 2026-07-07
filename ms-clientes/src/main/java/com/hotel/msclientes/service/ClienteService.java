package com.hotel.msclientes.service;


import com.hotel.msclientes.dto.ClienteRequest;
import com.hotel.msclientes.model.Clientes;
import com.hotel.msclientes.repository.ClienteRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Data
@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public Clientes crearCliente(ClienteRequest request) {
        Clientes cliente = new Clientes();
        cliente.setNombre(request.getNombre());
        cliente.setApellido(request.getApellido());
        cliente.setEmail(request.getEmail());
        cliente.setTelefono(request.getTelefono());
        cliente.setDireccion(request.getDireccion());
        cliente.setPreferencias(request.getPreferencias());
        return clienteRepository.save(cliente);
    }

    public List<Clientes> listarClientes() {
        return clienteRepository.findAll();
    }

    public Optional<Clientes> obtenerCliente(Long id) {
        return clienteRepository.findById(id);
    }

    public Optional<Clientes> obtenerClientePorEmail(String email) {
        return clienteRepository.findByEmail(email);
    }

    public Clientes actualizarCliente(Long id, ClienteRequest request) {
        Optional<Clientes> cliente = clienteRepository.findById(id);
        if (cliente.isPresent()) {
            Clientes c = cliente.get();
            c.setNombre(request.getNombre());
            c.setApellido(request.getApellido());
            c.setEmail(request.getEmail());
            c.setTelefono(request.getTelefono());
            c.setDireccion(request.getDireccion());
            c.setPreferencias(request.getPreferencias());
            return clienteRepository.save(c);
        }
        throw new RuntimeException("Cliente no encontrado");
    }

    public void eliminarCliente(Long id) {
        clienteRepository.deleteById(id);
    }


}