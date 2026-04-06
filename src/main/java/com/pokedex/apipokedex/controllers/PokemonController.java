package com.pokedex.apipokedex.controllers;
//Paquetes importados
import com.pokedex.apipokedex.dto.EntrenadorYNumPokemonDTO;
import com.pokedex.apipokedex.entities.Pokemon;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.pokedex.apipokedex.repositories.PokemonRepository;
import org.springframework.data.domain.Pageable; //ESTE ES EL PAQUETE CORRECTO NO EL DE PRINT

import java.security.KeyStore;
import java.util.Optional;

//Etiquetas basicas para controladores
@RestController
@RequestMapping("/api/pokemons")
public class PokemonController {
    //crear repositorio pokemon y con la etiqueta Autowired spring se encarga de inicializarlo y gestionarlo todo el tiempo necesario
    @Autowired
    PokemonRepository pokemonRepository;

    //Métodos
    //obtener datos (general)
    //Uso de pageable RECUERDA PONER PAGE EN LUGAR DE LIST!
    @GetMapping
    public ResponseEntity<Page<Pokemon>> findAllPokemon(@PageableDefault(page = 0, size = 5, sort = "nombre", direction = Sort.Direction.ASC) Pageable pageable) { //uso de response entity para dar una respuesta con el codigo correcto en este caso 200 q es OK
        return ResponseEntity.ok(pokemonRepository.findAll(pageable));
    }


    //obtener datos Pokemon por numPokedex
    @GetMapping("/{numPokedex}")
    public ResponseEntity<Pokemon> findByNumPokedex(@PathVariable Long numPokedex) {
        return pokemonRepository.findByNumPokedex(numPokedex)
                .map(pokemon -> ResponseEntity.ok(pokemon))//si encuentra el pokemon por id sale un codigo 200
                .orElseGet(() -> ResponseEntity.notFound().build());//si no encuentra el pokemon devuelve un codigo 400 (hay metodos para los que hará falta ponber el built y otros que no)
    }



    //insertar datos
    @PostMapping
    public ResponseEntity<Pokemon> createAula(@Valid @RequestBody Pokemon pokemon) {
        Pokemon pokemonGuardado = pokemonRepository.save(pokemon);
        // Devolvemos 201 Created, que es la mejor práctica para POST
        return ResponseEntity.status(201).body(pokemonGuardado);
    }

    //actualizar datos
    @PutMapping("/{numPokedex}")
    public ResponseEntity<Pokemon> updatePokemon(@Valid @RequestBody Pokemon pokemonNuevo, @PathVariable Long numPokedex) {
        Optional<Pokemon> pokemon = pokemonRepository.findByNumPokedex(numPokedex);
        if (pokemon.isPresent()) {
            pokemon.get().setNombre(pokemonNuevo.getNombre());
            pokemon.get().setNumPokedex(pokemonNuevo.getNumPokedex()); //usamos IS no GET porque es un booleano, tiene que darnos un si o no (true o false)
            pokemon.get().setDescripcion(pokemonNuevo.getDescripcion());
            pokemonRepository.save(pokemon.get());
            return ResponseEntity.ok(pokemon.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    //borrar datos
    @DeleteMapping("/{numPokedex}")
    public ResponseEntity<?> deletePokemon(@PathVariable Long numPokedex) {

        return pokemonRepository.findByNumPokedex(numPokedex)
                .map(pokemon -> {
                    //Borramos las relaciones con los tipos
                    pokemon.getTipos().forEach(tipo -> {
                        tipo.getPokemons().remove(pokemon);
                    });
                    pokemon.getTipos().clear();

                    pokemonRepository.deleteByNumPokedex(numPokedex);
                    return ResponseEntity.noContent().build();
                })
                .orElseGet(() -> {
                    return ResponseEntity.notFound().build();
                });

    }
}
