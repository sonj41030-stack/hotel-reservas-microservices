package com.hotel.msclientes;

import com.hotel.msclientes.dto.ClienteRequest;
import com.hotel.msclientes.model.Clientes;
import com.hotel.msclientes.repository.ClienteRepository;
import com.hotel.msclientes.service.ClienteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MsClientesApplicationTests {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    @Test
    @DisplayName("Debería crear un cliente correctamente cuando los datos son válidos")
    void deberiaCrearClienteCorrectamente() {
        // Given
        ClienteRequest request = new ClienteRequest();
        request.setNombre("Edixon");
        request.setApellido("Cabriles");
        request.setEmail("edixon@mail.com");
        request.setTelefono("912345678");
        request.setDireccion("Melipilla");
        request.setPreferencias("Piso alto");

        Clientes clienteGuardado = new Clientes();
        clienteGuardado.setId(1L);
        clienteGuardado.setNombre(request.getNombre());
        clienteGuardado.setApellido(request.getApellido());
        clienteGuardado.setEmail(request.getEmail());

        when(clienteRepository.save(any(Clientes.class))).thenReturn(clienteGuardado);

        // When
        Clientes resultado = clienteService.crearCliente(request);

        // Then
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("edixon@mail.com", resultado.getEmail());
        verify(clienteRepository, times(1)).save(any(Clientes.class));
    }

    @Test
    @DisplayName("Debería listar todos los clientes existentes")
    void deberiaListarTodosLosClientes() {
        // Given
        Clientes c1 = new Clientes();
        c1.setId(1L);
        Clientes c2 = new Clientes();
        c2.setId(2L);

        when(clienteRepository.findAll()).thenReturn(Arrays.asList(c1, c2));

        // When
        List<Clientes> lista = clienteService.listarClientes();

        // Then
        assertNotNull(lista);
        assertEquals(2, lista.size());
        verify(clienteRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería obtener un cliente por ID si existe")
    void deberiaObtenerClientePorIdExistente() {
        // Given
        Clientes cliente = new Clientes();
        cliente.setId(1L);
        cliente.setNombre("Juan");

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        // When
        Optional<Clientes> resultado = clienteService.obtenerCliente(1L);

        // Then
        assertTrue(resultado.isPresent());
        assertEquals("Juan", resultado.get().getNombre());
    }

    @Test
    @DisplayName("Debería retornar vacío si el cliente por ID no existe")
    void deberiaRetornarVacioSiClienteNoExiste() {
        // Given
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        Optional<Clientes> resultado = clienteService.obtenerCliente(99L);

        // Then
        assertFalse(resultado.isPresent());
    }

    @Test
    @DisplayName("Debería eliminar un cliente correctamente")
    void deberiaEliminarCliente() {
        // Given
        Long idEliminar = 1L;
        doNothing().when(clienteRepository).deleteById(idEliminar);

        // When
        assertDoesNotThrow(() -> clienteService.eliminarCliente(idEliminar));

        // Then
        verify(clienteRepository, times(1)).deleteById(idEliminar);
    }
}