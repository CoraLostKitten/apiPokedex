package com.pokedex.apipokedex.controllers;

import com.pokedex.apipokedex.dto.EntrenadorYNumPokemonDTO;
import com.pokedex.apipokedex.entities.Entrenador;
import com.pokedex.apipokedex.services.EntrenadorService;
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
@RequestMapping("/api/entrenadores")
@RequiredArgsConstructor // Inyectamos el servicio limpiamente
public class EntrenadorController {

    private final EntrenadorService entrenadorService;

    // 1. Obtener datos (general)
    @GetMapping
    public ResponseEntity<Page<Entrenador>> findAllEntrenadores(
            @PageableDefault(page= 0, size =5, sort = "nombre", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(entrenadorService.obtenerTodos(pageable));
    }

    // 2. Obtener datos entrenador por id
    @GetMapping("/{id}")
    public ResponseEntity<Entrenador> findById(@PathVariable Long id) {
        return entrenadorService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 3. GET resumen (DTO)
    @GetMapping("/resumen")
    public ResponseEntity<Page<EntrenadorYNumPokemonDTO>> getResumen(
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        Page<EntrenadorYNumPokemonDTO> resumen = entrenadorService.obtenerResumen(pageable);
        return resumen.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(resumen);
    }

    // 4. Insertar datos
    @PostMapping
    public ResponseEntity<Entrenador> createEntrenador(@Valid @RequestBody Entrenador entrenador) {
        Entrenador entrenadorGuardado = entrenadorService.guardarEntrenador(entrenador);
        return ResponseEntity.status(HttpStatus.CREATED).body(entrenadorGuardado);
    }

    // 5. Actualizar datos
    @PutMapping("/{id}")
    public ResponseEntity<Entrenador> updateEntrenador(@Valid @RequestBody Entrenador entrenadorNuevo, @PathVariable Long id) {
        return entrenadorService.actualizarEntrenador(id, entrenadorNuevo)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 6. Borrar datos
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEntrenador(@PathVariable Long id) {
        boolean eliminado = entrenadorService.borrarEntrenador(id);
        if (eliminado) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
