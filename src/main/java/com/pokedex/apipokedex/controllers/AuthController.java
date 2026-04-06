package com.pokedex.apipokedex.controllers;
import com.pokedex.apipokedex.dto.RegisterRequest;
import com.pokedex.apipokedex.dto.LoginRequest;
import com.pokedex.apipokedex.entities.Usuario;
import com.pokedex.apipokedex.repositories.UsuarioRepository;
import com.pokedex.apipokedex.services.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // Autenticar al usuario con email y password
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password())
            );


            // Si las credenciales son correctas, generar token
            String token = jwtService.generateToken(authentication);
            return ResponseEntity.ok(Map.of("token", token));

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
            // Verificar si el email ya existe
            if (usuarioRepository.findByEmail(registerRequest.email()).isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "El email ya está registrado"));
            }

            //Comprobación ambas contraseñas del registro son iguales
            if (!registerRequest.password().equals(registerRequest.password2())){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Las contraseñas no son iguales"));
            }


            // Crear nuevo usuario
            Usuario usuario = new Usuario();
            usuario.setEmail(registerRequest.email());
            usuario.setPassword(passwordEncoder.encode(registerRequest.password()));  // Cifrar password
            //modificacion parapoder hacer un admin
            if (registerRequest.roles() != null && !registerRequest.roles().isEmpty()) {
                usuario.setRoles(registerRequest.roles());
            } else {
                usuario.setRoles("ROLE_USER");
            }
            usuario.setEnabled(true);

            usuarioRepository.save(usuario);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Usuario registrado correctamente"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al registrar usuario"));
        }
    }
}

