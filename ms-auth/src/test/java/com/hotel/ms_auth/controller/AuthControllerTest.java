package com.hotel.ms_auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.ms_auth.dto.LoginRequest;
import com.hotel.ms_auth.dto.RegisterRequest;
import com.hotel.ms_auth.model.Rol;
import com.hotel.ms_auth.model.Usuario;
import com.hotel.ms_auth.repository.UsuarioRepository;
import com.hotel.ms_auth.security.JwtUtil;
import com.hotel.ms_auth.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean; // Nuevo import oficial para Spring Boot 3.4
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean // Actualizado para evitar el warning de 'deprecated'
    private UsuarioRepository usuarioRepository;

    @MockitoBean // Actualizado para evitar el warning de 'deprecated'
    private AuthService authService;

    @MockitoBean // Actualizado para evitar el warning de 'deprecated'
    private JwtUtil jwtUtil;

    @Test
    @DisplayName("Debe registrar un usuario")
    void registrarUsuario() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setNombre("Juan");
        request.setEmail("juan@mail.com");
        request.setPassword("123456");

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Juan");
        usuario.setEmail("juan@mail.com");
        usuario.setRol(Rol.CLIENTE);

        when(authService.registrar(any(Usuario.class))).thenReturn(usuario);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("juan@mail.com"));
    }

    @Test
    @DisplayName("Debe iniciar sesión correctamente")
    void loginCorrecto() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("juan@mail.com");
        request.setPassword("123456");

        Usuario usuario = new Usuario();
        usuario.setEmail("juan@mail.com");
        usuario.setRol(Rol.CLIENTE);

        when(authService.login("juan@mail.com", "123456"))
                .thenReturn(Optional.of(usuario));

        when(jwtUtil.generarToken("juan@mail.com", "CLIENTE"))
                .thenReturn("token123");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("token123"));
    }

    @Test
    @DisplayName("Debe devolver 401 cuando el login falla")
    void loginIncorrecto() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("juan@mail.com");
        request.setPassword("123");

        when(authService.login("juan@mail.com", "123"))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Debe listar usuarios")
    void listarUsuarios() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Juan");

        when(usuarioRepository.findAll()).thenReturn(List.of(usuario));

        mockMvc.perform(get("/auth/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Juan"));
    }

    @Test
    @DisplayName("Debe obtener usuario por id")
    void obtenerUsuario() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Juan");

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        mockMvc.perform(get("/auth/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Juan"));
    }

    @Test
    @DisplayName("Debe devolver 404 si el usuario no existe por id")
    void obtenerUsuarioNoExiste() throws Exception {
        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/auth/usuarios/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Debe actualizar un usuario existente")
    void actualizarUsuario() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setNombre("Juan Modificado");
        request.setEmail("juan@mail.com");
        request.setPassword("nuevaPassword");

        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setId(1L);
        usuarioExistente.setNombre("Juan");
        usuarioExistente.setEmail("juan@mail.com");

        Usuario usuarioActualizado = new Usuario();
        usuarioActualizado.setId(1L);
        usuarioActualizado.setNombre("Juan Modificado");
        usuarioActualizado.setEmail("juan@mail.com");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioExistente));
        when(authService.encriptarPassword("nuevaPassword")).thenReturn("hashedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioActualizado);

        mockMvc.perform(put("/auth/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Juan Modificado"));
    }

    @Test
    @DisplayName("Debe eliminar usuario")
    void eliminarUsuario() throws Exception {
        when(usuarioRepository.existsById(1L)).thenReturn(true);

        mockMvc.perform(delete("/auth/usuarios/1"))
                .andExpect(status().isOk());

        verify(usuarioRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Debe devolver 404 al eliminar usuario inexistente")
    void eliminarUsuarioNoExiste() throws Exception {
        when(usuarioRepository.existsById(99L)).thenReturn(false);

        mockMvc.perform(delete("/auth/usuarios/99"))
                .andExpect(status().isNotFound());

        verify(usuarioRepository, never()).deleteById(any());
    }
}