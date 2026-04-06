package com.pokedex.apipokedex.controllers;
//Paquetes importados
import com.pokedex.apipokedex.dto.EntrenadorYNumPokemonDTO;
import com.pokedex.apipokedex.entities.Entrenador;
import com.pokedex.apipokedex.entities.Pokemon;
import com.pokedex.apipokedex.repositories.PokemonRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.pokedex.apipokedex.repositories.EntrenadorRepository;
import java.util.Optional;

//Etiquetas basicas para controladores
@RestController
@RequestMapping("/api/entrenadores")
public class EntrenadorController {
    //crear repositorio entrenador y con la etiqueta Autowired spring se encarga de inicializarlo y gestionarlo todo el tiempo necesario
    @Autowired
    EntrenadorRepository entrenadorRepository;

    //insertar repo pomemon para poder desvincular en el borrado
    @Autowired
    PokemonRepository pokemonRepository;

    //Métodos
    //obtener datos (general)
    @GetMapping
    public ResponseEntity<Page<Entrenador>> findAllEntrenadores(
            // Abrimos paréntesis del método
            @PageableDefault(page= 0, size =5, sort = "nombre", direction = Sort.Direction.ASC) Pageable pageable
            // Cerramos paréntesis del método AQUÍ, después de la variable
    ) {
        return ResponseEntity.ok(entrenadorRepository.findAll(pageable));
    }


    //obtener datos entrenador por id
    @GetMapping ("/{id}")
    public ResponseEntity <Entrenador> findById(@PathVariable Long id) {
        return entrenadorRepository.findById(id)
                .map(pokemon -> ResponseEntity.ok(pokemon))//si encuentra el entrenador por id sale un codigo 200
                .orElseGet(() -> ResponseEntity.notFound().build());//si no encuentra el aulentrenador  devuelve un codigo 400 (hay metodos para los que hará falta ponber el built y otros que no)
    }

    // 2. GET resumen (DTO)
    @GetMapping("/resumen")
    public ResponseEntity<Page<EntrenadorYNumPokemonDTO>> getResumen(
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        Page<EntrenadorYNumPokemonDTO> resumen = entrenadorRepository.obtenerNumPokemonEntrenador(pageable);
        return resumen.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(resumen);
    }


    //insertar datos
    @PostMapping
    public ResponseEntity<Entrenador> createAula(@Valid @RequestBody Entrenador entrenador) {
        Entrenador entrenadorGuardado = entrenadorRepository.save(entrenador);
        // Devolvemos 201 Created, que es la mejor práctica para POST
        return ResponseEntity.status(201).body(entrenadorGuardado);
    }

    //actualizar datos
    @PutMapping ("/{id}")
    public ResponseEntity<Entrenador> updateEntrenador(@Valid @RequestBody Entrenador entrenadorNuevo, @PathVariable Long id) {
        Optional<Entrenador> entrenador = entrenadorRepository.findById(id);
        if (entrenador.isPresent()) {
            entrenador.get().setNombre(entrenadorNuevo.getNombre());
            entrenador.get().setCiudad(entrenadorNuevo.getCiudad());
            entrenadorRepository.save(entrenador.get());
            return ResponseEntity.ok(entrenador.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    //borrar datos
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEntrenador(@PathVariable Long id) {

        return entrenadorRepository.findById(id)
                .map(entrenadorEncontrado -> {

                    // 1. Recorremos sus pokemons para romper la relación
                    // Usamos una variable auxiliar 'poke' para no liarnos
                    entrenadorEncontrado.getEquipo().forEach(poke -> {
                        poke.setEntrenador(null); // Le quitamos el dueño
                        pokemonRepository.save(poke); // <--- ¡IMPORTANTE! Guardamos el cambio en la BD
                    });

                    // 2. Ahora que los pokemons son libres, podemos borrar al entrenador
                    entrenadorRepository.delete(entrenadorEncontrado);

                    return ResponseEntity.noContent().<Void>build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}
