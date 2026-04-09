package com.pokedex.apipokedex.services;

import com.pokedex.apipokedex.entities.Pokemon;
import com.pokedex.apipokedex.entities.Tipo;
import com.pokedex.apipokedex.repositories.PokemonRepository;
import com.pokedex.apipokedex.repositories.TipoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PokemonService {

    private final PokemonRepository pokemonRepository;
    private final TipoRepository tipoRepository;

    // 1. Obtener datos (Paginado)
    public Page<Pokemon> obtenerTodos(Pageable pageable) {
        return pokemonRepository.findAll(pageable);
    }

    // 2. Obtener por numPokedex
    public Optional<Pokemon> buscarPorNumPokedex(Long numPokedex) {
        return pokemonRepository.findByNumPokedex(numPokedex);
    }

    // 3. Insertar datos
    public Pokemon guardarPokemon(Pokemon pokemon) {
        return pokemonRepository.save(pokemon);
    }

    // MÉTODOS PARA ACTUALIZAR (UPDATE)
    @Transactional
    public Pokemon actualizarPokemon(Long id, Pokemon detallesPokemon) {
        Pokemon pokemonExistente = pokemonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No encontrado"));

        pokemonExistente.setNombre(detallesPokemon.getNombre());
        pokemonExistente.setNumPokedex(detallesPokemon.getNumPokedex());
        pokemonExistente.setNivel(detallesPokemon.getNivel());
        pokemonExistente.setDescripcion(detallesPokemon.getDescripcion());

        // Limpiamos los tipos actuales
        pokemonExistente.getTipos().clear();

        // Si vienen tipos en la petición...
        if (detallesPokemon.getTipos() != null) {
            for (Tipo t : detallesPokemon.getTipos()) {
                // Buscamos el tipo real en la base de datos por el ID que viene en el JSON
                if (t.getId() != null) {
                    Tipo tipoBD = tipoRepository.findById(t.getId())
                            .orElseThrow(() -> new RuntimeException("Tipo no existe"));
                    pokemonExistente.getTipos().add(tipoBD);
                }
            }
        }

        return pokemonRepository.save(pokemonExistente);
    }
    // 5. Borrar datos desvinculando relaciones
    @Transactional
    public boolean borrarPokemon(Long id) {
        // BUSCAMOS POR ID (Clave primaria), no por numPokedex
        return pokemonRepository.findById(id).map(pokemon -> {

            // 1. Borramos las relaciones con los tipos (¡Esta parte la tenías genial!)
            // Ojo: Solo necesitas hacer esto si en tu entidad Tipo tienes un 'mappedBy'
            pokemon.getTipos().forEach(tipo -> tipo.getPokemons().remove(pokemon));
            pokemon.getTipos().clear();

            // 2. Borramos la entidad directamente, es más seguro que usar un deleteBy...
            pokemonRepository.delete(pokemon);

            return true;
        }).orElse(false); // Si no encuentra el ID, devuelve false
    }
    // Método para buscar un solo Pokémon por su ID
    public Pokemon obtenerPokemonPorId(Long id) {
        return pokemonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pokémon no encontrado con ID: " + id));
    }
}