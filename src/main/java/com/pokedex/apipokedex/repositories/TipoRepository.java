package com.pokedex.apipokedex.repositories;
//paquetes importados
import com.pokedex.apipokedex.entities.Tipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
//Etiqueta para indicar que es un repositorio
@Repository
public interface TipoRepository extends JpaRepository<Tipo, Long> {
}
