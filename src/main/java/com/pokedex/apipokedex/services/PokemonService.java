package com.pokedex.apipokedex.services;

import com.pokedex.apipokedex.entities.Pokemon;
import com.pokedex.apipokedex.repositories.PokemonRepository;
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

    // 4. Actualizar datos
    @Transactional
    public Optional<Pokemon> actualizarPokemon(Long numPokedex, Pokemon pokemonNuevo) {
        return pokemonRepository.findByNumPokedex(numPokedex).map(pokemonExistente -> {
            pokemonExistente.setNombre(pokemonNuevo.getNombre());
            pokemonExistente.setNumPokedex(pokemonNuevo.getNumPokedex());
            pokemonExistente.setDescripcion(pokemonNuevo.getDescripcion());
            return pokemonRepository.save(pokemonExistente);
        });
    }

    // 5. Borrar datos desvinculando relaciones
    @Transactional
    public boolean borrarPokemon(Long numPokedex) {
        return pokemonRepository.findByNumPokedex(numPokedex).map(pokemon -> {
            // Borramos las relaciones con los tipos
            pokemon.getTipos().forEach(tipo -> tipo.getPokemons().remove(pokemon));
            pokemon.getTipos().clear();

            pokemonRepository.deleteByNumPokedex(numPokedex);
            return true;
        }).orElse(false);
    }
}