package com.hotel.ms_auth.service;

import com.hotel.ms_auth.model.Rol;
import com.hotel.ms_auth.model.Usuario;
import com.hotel.ms_auth.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService service;

    @Test
    @DisplayName("registrar() debe encriptar la password y asignar rol CLIENTE")
    void deberiaEncriptarPasswordYAsignarRolClienteAlRegistrar() {
        // Given
        Usuario usuario = new Usuario();
        usuario.setNombre("Juan");
        usuario.setEmail("juan@mail.com");
        usuario.setPassword("123456");

        when(passwordEncoder.encode("123456")).thenReturn("hashEncriptado");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        Usuario resultado = service.registrar(usuario);

        // Then
        assertNotNull(resultado);
        assertEquals("hashEncriptado", resultado.getPassword());
        assertEquals(Rol.CLIENTE, resultado.getRol());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    @DisplayName("login() debe retornar el usuario cuando las credenciales son correctas")
    void deberiaRetornarUsuario_cuandoLoginConCredencialesCorrectas() {
        // Given
        Usuario usuario = new Usuario();
        usuario.setEmail("juan@mail.com");
        usuario.setPassword("hashEncriptado");

        when(usuarioRepository.findByEmail("juan@mail.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("123456", "hashEncriptado")).thenReturn(true);

        // When
        Optional<Usuario> resultado = service.login("juan@mail.com", "123456");

        // Then
        assertTrue(resultado.isPresent());
        assertEquals(usuario, resultado.get());
    }

    @Test
    @DisplayName("login() debe retornar vacío cuando la password es incorrecta")
    void deberiaRetornarVacio_cuandoLoginConPasswordIncorrecta() {
        // Given
        Usuario usuario = new Usuario();
        usuario.setEmail("juan@mail.com");
        usuario.setPassword("hashEncriptado");

        when(usuarioRepository.findByEmail("juan@mail.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("passwordIncorrecta", "hashEncriptado")).thenReturn(false);

        // When
        Optional<Usuario> resultado = service.login("juan@mail.com", "passwordIncorrecta");

        // Then
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("login() debe retornar vacío cuando el usuario no existe")
    void deberiaRetornarVacio_cuandoUsuarioNoExiste() {
        // Given
        when(usuarioRepository.findByEmail("noexiste@mail.com")).thenReturn(Optional.empty());

        // When
        Optional<Usuario> resultado = service.login("noexiste@mail.com", "123456");

        // Then
        assertTrue(resultado.isEmpty());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("encriptarPassword() debe delegar en el PasswordEncoder y retornar el hash")
    void deberiaRetornarPasswordEncriptada() {
        // Given
        when(passwordEncoder.encode("123456")).thenReturn("hashEncriptado");

        // When
        String resultado = service.encriptarPassword("123456");

        // Then
        assertEquals("hashEncriptado", resultado);
        verify(passwordEncoder).encode("123456");
    }
}