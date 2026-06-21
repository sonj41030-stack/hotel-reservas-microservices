package com.hotel.ms_auth.service;

import com.hotel.ms_auth.model.Usuario;
import com.hotel.ms_auth.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private  UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService service;

    @Test
    void deberiaRetornarUsuarioAlRegistrar() {

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Juan");

        Mockito.when(usuarioRepository.save(usuario))
                .thenReturn(usuario);

        Usuario resultado = service.registrar(usuario);

        assertNotNull(resultado);
        assertEquals("Juan", resultado.getNombre());

        verify(usuarioRepository).save(usuario);
    }



    public Optional<Usuario> login(String email, String password){
        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
        if(usuario.isPresent()){
            if(passwordEncoder.matches(password, usuario.get().getPassword())){
                return usuario;
            }
        }
        return Optional.empty();
    }


}