package com.hotel.ms_auth.controller;

import com.hotel.ms_auth.dto.LoginRequest;
import com.hotel.ms_auth.dto.RegisterRequest;
import com.hotel.ms_auth.model.Usuario;
import com.hotel.ms_auth.repository.UsuarioRepository;
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

    private final UsuarioRepository usuarioRepository;
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
    @GetMapping("/usuarios")
    public ResponseEntity<?> listarUsuarios() {
        return ResponseEntity.ok(usuarioRepository.findAll());
    }

    @GetMapping("/usuarios/{id}")
    public ResponseEntity<?> obtenerUsuario(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        if (usuario.isPresent()) {
            return ResponseEntity.ok(usuario.get());
        }
        return ResponseEntity.status(404).body("Usuario no encontrado");
    }

    @PutMapping("/usuarios/{id}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Long id, @Valid @RequestBody RegisterRequest request) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        if (usuario.isPresent()) {
            Usuario u = usuario.get();
            u.setNombre(request.getNombre());
            u.setEmail(request.getEmail());
            u.setPassword(authService.encriptarPassword(request.getPassword()));
            return ResponseEntity.ok(usuarioRepository.save(u));
        }
        return ResponseEntity.status(404).body("Usuario no encontrado");
    }

    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
            return ResponseEntity.ok("Usuario eliminado correctamente");
        }
        return ResponseEntity.status(404).body("Usuario no encontrado");
    }
}