package com.pokedex.apipokedex.repositories;
//paquetes importados
import com.pokedex.apipokedex.dto.EntrenadorYNumPokemonDTO;
import com.pokedex.apipokedex.entities.Entrenador;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


//etiqueta para indicar que es un repository
@Repository
public interface EntrenadorRepository extends JpaRepository<Entrenador, Long> {
    //CONSULTAS CON QUERY
    // Como la consulta empieza mirando la tabla 'Entrenador', va aquí.
    @Query("SELECT new com.pokedex.apipokedex.dto.EntrenadorYNumPokemonDTO(e.nombre, SIZE(e.equipo)) FROM Entrenador e")
    Page<EntrenadorYNumPokemonDTO> obtenerNumPokemonEntrenador(Pageable pageable);
// ^^^ Fíjate que devuelva Page, no List, para que funcione el controlador.
}
