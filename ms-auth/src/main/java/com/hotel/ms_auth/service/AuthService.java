package com.hotel.ms_auth.service;

import com.hotel.ms_auth.model.Rol;
import com.hotel.ms_auth.model.Usuario;
import com.hotel.ms_auth.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private  final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public Usuario registrar(Usuario usuario){
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuario.setRol(Rol.CLIENTE);
        return  usuarioRepository.save(usuario);
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
