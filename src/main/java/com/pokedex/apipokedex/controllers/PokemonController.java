package com.pokedex.apipokedex.controllers;

import com.pokedex.apipokedex.entities.Pokemon;
import com.pokedex.apipokedex.repositories.TipoRepository;
import com.pokedex.apipokedex.services.PokemonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pokemon")
@RequiredArgsConstructor // Inyección limpia sin @Autowired para todos los atributos 'final'
public class PokemonController {

    // 1. INYECCIÓN DE DEPENDENCIAS (Todas ordenadas arriba)
    private final PokemonService pokemonService;
    private final TipoRepository tipoRepository;

    // ==========================================
    // 2. MÉTODOS GET (Lectura)
    // ==========================================

    // Obtener datos (general con paginación)
    @GetMapping
    public ResponseEntity<Page<Pokemon>> findAllPokemon(
            @PageableDefault(page = 0, size = 5, sort = "nombre", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(pokemonService.obtenerTodos(pageable));
    }

    // Ruta para que React pida los datos de un solo Pokémon antes de editarlo
    @GetMapping("/{id}")
    public ResponseEntity<?> getPokemonById(@PathVariable Long id) {
        try {
            Pokemon pokemon = pokemonService.obtenerPokemonPorId(id);
            return ResponseEntity.ok(pokemon);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Obtener datos Pokemon por numPokedex
    @GetMapping("/numero/{numPokedex}")
    public ResponseEntity<Pokemon> findByNumPokedex(@PathVariable Long numPokedex) {
        return pokemonService.buscarPorNumPokedex(numPokedex)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Obtener todos los tipos para los checkboxes de React
    @GetMapping("/tipos")
    public ResponseEntity<?> getTodosLosTipos() {
        return ResponseEntity.ok(tipoRepository.findAll());
    }

    // ==========================================
    // 3. MÉTODOS POST, PUT, DELETE (Escritura)
    // ==========================================

    // Insertar datos
    @PostMapping
    public ResponseEntity<Pokemon> createPokemon(@Valid @RequestBody Pokemon pokemon) {
        Pokemon pokemonGuardado = pokemonService.guardarPokemon(pokemon);
        return ResponseEntity.status(HttpStatus.CREATED).body(pokemonGuardado);
    }

    // Actualizar datos
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarPokemon(@PathVariable Long id, @RequestBody Pokemon pokemon) {
        try {
            Pokemon pokemonActualizado = pokemonService.actualizarPokemon(id, pokemon);
            return ResponseEntity.ok(pokemonActualizado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al actualizar el Pokémon: " + e.getMessage());
        }
    }

    // Borrar datos
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePokemon(@PathVariable Long id) {
        boolean eliminado = pokemonService.borrarPokemon(id);
        if (eliminado) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}