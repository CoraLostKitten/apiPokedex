package com.pokedex.apipokedex.controllers;

import com.pokedex.apipokedex.entities.Pokemon;
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
@RequestMapping("/api/pokemons")
@RequiredArgsConstructor // Inyección limpia sin @Autowired
public class PokemonController {

    private final PokemonService pokemonService;

    // Obtener datos (general con paginación)
    @GetMapping
    public ResponseEntity<Page<Pokemon>> findAllPokemon(
            @PageableDefault(page = 0, size = 5, sort = "nombre", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(pokemonService.obtenerTodos(pageable));
    }

    // Obtener datos Pokemon por numPokedex
    @GetMapping("/{numPokedex}")
    public ResponseEntity<Pokemon> findByNumPokedex(@PathVariable Long numPokedex) {
        return pokemonService.buscarPorNumPokedex(numPokedex)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Insertar datos
    @PostMapping
    public ResponseEntity<Pokemon> createPokemon(@Valid @RequestBody Pokemon pokemon) {
        Pokemon pokemonGuardado = pokemonService.guardarPokemon(pokemon);
        return ResponseEntity.status(HttpStatus.CREATED).body(pokemonGuardado);
    }

    // Actualizar datos
    @PutMapping("/{numPokedex}")
    public ResponseEntity<Pokemon> updatePokemon(@Valid @RequestBody Pokemon pokemonNuevo, @PathVariable Long numPokedex) {
        return pokemonService.actualizarPokemon(numPokedex, pokemonNuevo)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Borrar datos
    @DeleteMapping("/{numPokedex}")
    public ResponseEntity<Void> deletePokemon(@PathVariable Long numPokedex) {
        boolean eliminado = pokemonService.borrarPokemon(numPokedex);
        if (eliminado) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}