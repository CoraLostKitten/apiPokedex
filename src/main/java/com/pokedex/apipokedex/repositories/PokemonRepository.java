package com.pokedex.apipokedex.repositories;
//Paquetes importados
import com.pokedex.apipokedex.dto.EntrenadorYNumPokemonDTO;
import com.pokedex.apipokedex.entities.Pokemon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

//Etiqueta para reposotiry
@Repository
public interface PokemonRepository extends JpaRepository<Pokemon, Long> {
    //Optional para UN objeto List para VARIOS
    //creacion de metodos personalizados para encontrar un pokemon a traves de su numero de pokedex
    Optional<Pokemon> findByNumPokedex(Long numPokedex);
    //borrar por numPokedex
    Optional<Pokemon> deleteByNumPokedex(Long numPokedex);
    //buscar pokem on por nivel
    Optional<Pokemon> findByNivel(Long nivel);
    //obtener todos los pokemon que incluyan una cadena de texto ignorando mayus y minus
    List<Pokemon> findByNombreContainingIgnoreCase (String nombre);


    //Consultas Query
    //listar pokem on con nivales superiores a 50
    @Query ("SELECT p FROM Pokemon p WHERE p.nivel > 50") //Consulta
    List<Pokemon> buscarPokemonFuertes(); //metodo que nos devuelve la consulta

    //listar pokemon con niveles entre 10 y 30
    @Query("SELECT p FROM Pokemon p WHERE p.nivel BETWEEN 10 AND 30")
    List<Pokemon> buscarPokemonIntermedios();

    // ¿Cuántos pokémon hay en total?
    @Query("SELECT COUNT(p) FROM Pokemon p")
    Long totalPokemons();





}