package com.pokedex.apipokedex.services;

import com.pokedex.apipokedex.dto.EntrenadorYNumPokemonDTO;
import com.pokedex.apipokedex.entities.Entrenador;
import com.pokedex.apipokedex.repositories.EntrenadorRepository;
import com.pokedex.apipokedex.repositories.PokemonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EntrenadorService {

    private final EntrenadorRepository entrenadorRepository;
    private final PokemonRepository pokemonRepository;

    // 1. Obtener datos (general paginado)
    public Page<Entrenador> obtenerTodos(Pageable pageable) {
        return entrenadorRepository.findAll(pageable);
    }

    // 2. Obtener datos por ID
    public Optional<Entrenador> buscarPorId(Long id) {
        return entrenadorRepository.findById(id);
    }

    // 3. GET resumen (DTO)
    public Page<EntrenadorYNumPokemonDTO> obtenerResumen(Pageable pageable) {
        return entrenadorRepository.obtenerNumPokemonEntrenador(pageable);
    }

    // 4. Insertar datos
    public Entrenador guardarEntrenador(Entrenador entrenador) {
        return entrenadorRepository.save(entrenador);
    }

    // 5. Actualizar datos
    @Transactional
    public Optional<Entrenador> actualizarEntrenador(Long id, Entrenador entrenadorNuevo) {
        return entrenadorRepository.findById(id).map(entrenadorExistente -> {
            entrenadorExistente.setNombre(entrenadorNuevo.getNombre());
            entrenadorExistente.setCiudad(entrenadorNuevo.getCiudad());
            return entrenadorRepository.save(entrenadorExistente);
        });
    }

    // 6. Borrar datos (desvinculando equipo)
    @Transactional
    public boolean borrarEntrenador(Long id) {
        return entrenadorRepository.findById(id).map(entrenadorEncontrado -> {
            // Recorremos sus pokemons para romper la relación
            entrenadorEncontrado.getEquipo().forEach(poke -> {
                poke.setEntrenador(null); // Le quitamos el dueño
                pokemonRepository.save(poke); // Guardamos el cambio en la BD
            });

            // Ahora que los pokemons son libres, podemos borrar al entrenador
            entrenadorRepository.delete(entrenadorEncontrado);
            return true;
        }).orElse(false);
    }
}