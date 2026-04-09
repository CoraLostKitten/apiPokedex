package com.pokedex.apipokedex.controllers;

import com.pokedex.apipokedex.dto.RegisterRequest;
import com.pokedex.apipokedex.dto.LoginRequest;
import com.pokedex.apipokedex.entities.Usuario;
import com.pokedex.apipokedex.services.JwtService;
import com.pokedex.apipokedex.services.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")  // URL: /api/auth/login, /api/auth/register
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioService usuarioService; // Usamos la nueva cocina

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password())
            );

            String token = jwtService.generateToken(authentication);

            // Buscamos los datos usando el servicio
            Usuario usuario = usuarioService.buscarPorEmail(loginRequest.email());

            // Devolvemos el JSON exacto que espera React
            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "user", Map.of(
                            "email", usuario.getEmail(),
                            "roles", usuario.getRoles()
                    )
            ));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Credenciales incorrectas"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error en el servidor"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            // El servicio hace TODO el trabajo sucio
            usuarioService.registrarUsuario(registerRequest);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Usuario registrado correctamente"));

        } catch (IllegalArgumentException e) {
            // Capturamos los errores de negocio (email duplicado, contraseñas mal...)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al registrar usuario"));
        }
    }
}

