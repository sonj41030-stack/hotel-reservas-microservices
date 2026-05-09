package com.hotel.msclientes.controller;

import com.hotel.msclientes.dto.ClienteRequest;
import com.hotel.msclientes.model.Clientes;
import com.hotel.msclientes.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @PostMapping
    public ResponseEntity<Clientes> crearCliente(@Valid @RequestBody ClienteRequest request) {
        return ResponseEntity.ok(clienteService.crearCliente(request));
    }

    @GetMapping
    public ResponseEntity<List<Clientes>> listarClientes() {
        return ResponseEntity.ok(clienteService.listarClientes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerCliente(@PathVariable Long id) {
        Optional<Clientes> cliente = clienteService.obtenerCliente(id);
        if (cliente.isPresent()) {
            return ResponseEntity.ok(cliente.get());
        }
        return ResponseEntity.status(404).body("Cliente no encontrado");
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<?> obtenerClientePorEmail(@PathVariable String email) {
        Optional<Clientes> cliente = clienteService.obtenerClientePorEmail(email);
        if (cliente.isPresent()) {
            return ResponseEntity.ok(cliente.get());
        }
        return ResponseEntity.status(404).body("Cliente no encontrado");
    }
    //importante
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarCliente(@PathVariable Long id, @Valid @RequestBody ClienteRequest request) {
        return ResponseEntity.ok(clienteService.actualizarCliente(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarCliente(@PathVariable Long id) {
        clienteService.eliminarCliente(id);
        return ResponseEntity.ok("Cliente eliminado correctamente");
    }
}