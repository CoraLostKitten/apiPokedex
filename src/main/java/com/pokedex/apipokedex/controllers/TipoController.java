package com.pokedex.apipokedex.controllers;

import com.pokedex.apipokedex.entities.Tipo;
import com.pokedex.apipokedex.services.TipoService; // Importamos el Service
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipos")
public class TipoController {

    @Autowired
    private TipoService tipoService; // <--- Usamos el Service, NO el Repository

    // Obtener todos
    @GetMapping
    public ResponseEntity<List<Tipo>> findAllTipos() {
        return ResponseEntity.ok(tipoService.obtenerTodos());
    }

    // Obtener por ID
    @GetMapping("/{id}")
    public ResponseEntity<Tipo> findById(@PathVariable Long id) {
        return tipoService.buscarPorId(id)
                .map(tipo -> ResponseEntity.ok(tipo)) // O .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Insertar
    @PostMapping
    public ResponseEntity<Tipo> createTipo(@Valid @RequestBody Tipo tipo) {
        // Delegamos la lógica (y las mayúsculas) al servicio
        Tipo tipoGuardado = tipoService.guardarTipo(tipo);
        return ResponseEntity.status(HttpStatus.CREATED).body(tipoGuardado);
    }

    // Actualizar
    @PutMapping("/{id}")
    public ResponseEntity<Tipo> updateTipo(@Valid @RequestBody Tipo tipoNuevo, @PathVariable Long id) {
        // El servicio se encarga de buscar, actualizar y guardar si existe
        return tipoService.actualizarTipo(id, tipoNuevo)
                .map(tipoActualizado -> ResponseEntity.ok(tipoActualizado))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Borrar
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTipo(@PathVariable Long id) {
        // El servicio se encarga de desvincular los pokemons antes de borrar
        boolean eliminado = tipoService.borrarTipo(id);

        if (eliminado) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}