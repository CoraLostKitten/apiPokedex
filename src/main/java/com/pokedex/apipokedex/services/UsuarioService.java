package com.pokedex.apipokedex.services;

import com.pokedex.apipokedex.dto.RegisterRequest;
import com.pokedex.apipokedex.entities.Usuario;
import com.pokedex.apipokedex.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Usuario registrarUsuario(RegisterRequest request) {
        // 1. Validaciones de negocio
        if (usuarioRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        if (!request.password().equals(request.password2())) {
            throw new IllegalArgumentException("Las contraseñas no coinciden");
        }

        // 2. Preparar el nuevo usuario
        Usuario usuario = new Usuario();
        usuario.setEmail(request.email());
        usuario.setPassword(passwordEncoder.encode(request.password())); // Cifrado vital

        // Asignación de roles y valores por defecto
        if (request.roles() != null && !request.roles().isEmpty()) {
            usuario.setRoles(request.roles());
        } else {
            usuario.setRoles("ROLE_USER");
        }

        usuario.setEnabled(true);
        usuario.setFechaRegistro(LocalDate.now()); // Guardamos la fecha actual

        // 3. Guardar en la base de datos
        return usuarioRepository.save(usuario);
    }

    // Método auxiliar para buscar al usuario en el login y no usar el Repo en el Controller
    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email).orElseThrow();
    }
}