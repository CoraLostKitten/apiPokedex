package com.pokedex.apipokedex.dto;

public record EntrenadorYNumPokemonDTO(
        String nombre,
        Long numPokemon // Cambia a Long si lo tenías como Integer o int
) {
    // Hibernate a veces necesita que el constructor sea explícito en los records
    public EntrenadorYNumPokemonDTO(String nombre, Long numPokemon) {
        this.nombre = nombre;
        this.numPokemon = numPokemon;
    }
}