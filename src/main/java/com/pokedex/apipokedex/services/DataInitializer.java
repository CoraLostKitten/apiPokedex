package com.pokedex.apipokedex.services;

import com.pokedex.apipokedex.entities.Usuario;
import com.pokedex.apipokedex.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Solo creamos el usuario si la base de datos está vacía
        if (usuarioRepository.count() == 0) {
            Usuario admin = new Usuario();

            admin.setEmail("admin@pokedex.com");
            admin.setPassword(passwordEncoder.encode("admin")); // "admin" encriptado
            admin.setNombre("Admin");
            admin.setApellidos("Pokedex");
            admin.setEnabled(true);
            admin.setRoles("ROLE_ADMIN");

            // AQUÍ EL ARREGLO:
            admin.setFechaRegistro(LocalDate.now());

            usuarioRepository.save(admin);

            System.out.println("--------------------------------------");
            System.out.println("✅ USUARIO CREADO CON ÉXITO EN LA BD");
            System.out.println("Email: admin@pokedex.com | Pass: admin");
            System.out.println("--------------------------------------");
        }
    }
}
