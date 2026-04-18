package com.hotel.ms_auth.controller;

import com.hotel.ms_auth.dto.LoginRequest;
import com.hotel.ms_auth.dto.RegisterRequest;
import com.hotel.ms_auth.model.Usuario;
import com.hotel.ms_auth.security.JwtUtil;
import com.hotel.ms_auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(request.getPassword());

        Usuario guardado = authService.registrar(usuario);
        return ResponseEntity.ok(guardado);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        Optional<Usuario> usuario = authService.login(request.getEmail(), request.getPassword());
        if (usuario.isPresent()) {
            String token = jwtUtil.generarToken(
                    usuario.get().getEmail(),
                    usuario.get().getRol().name()
            );
            return ResponseEntity.ok(token);
        }
        return ResponseEntity.status(401).body("Credenciales incorrectas");
    }
}