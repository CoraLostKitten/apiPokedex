package com.pokedex.apipokedex.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EntrenadorYNumPokemonDTO {

    private String nombre;
    private Long cantidadPokemons;

}
