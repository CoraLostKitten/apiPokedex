package com.pokedex.apipokedex.services;

import com.pokedex.apipokedex.entities.Tipo;
import com.pokedex.apipokedex.repositories.TipoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TipoService {

    @Autowired
    private TipoRepository tipoRepository;

    // 1. Listar todos
    public List<Tipo> obtenerTodos() {
        return tipoRepository.findAll();
    }

    // 2. Buscar por ID
    public Optional<Tipo> buscarPorId(Long id) {
        return tipoRepository.findById(id);
    }

    // 3. Guardar (Create)
    public Tipo guardarTipo(Tipo tipo) {
        // Regla de negocio: Guardar siempre en mayúsculas
        if (tipo.getNombre() != null) {
            tipo.setNombre(tipo.getNombre().toUpperCase());
        }
        return tipoRepository.save(tipo);
    }

    // 4. Actualizar (Update) - ¡NUEVO!
    public Optional<Tipo> actualizarTipo(Long id, Tipo tipoNuevo) {
        // Buscamos si existe
        return tipoRepository.findById(id).map(tipoExistente -> {
            // Actualizamos los campos
            if (tipoNuevo.getNombre() != null) {
                tipoExistente.setNombre(tipoNuevo.getNombre().toUpperCase());
            }
            // Guardamos
            return tipoRepository.save(tipoExistente);
        });
    }

    // 5. Borrar (Delete) - ¡CORREGIDO CON TU LÓGICA!
    @Transactional // Importante para operaciones que tocan varias tablas (JoinTable)
    public boolean borrarTipo(Long id) {
        Optional<Tipo> tipoOpt = tipoRepository.findById(id);

        if (tipoOpt.isPresent()) {
            Tipo tipo = tipoOpt.get();

            // --- TU LÓGICA DE DESVINCULACIÓN ---
            // Recorremos los pokemons que tienen este tipo y se lo quitamos
            // Esto borra la fila en la tabla intermedia "pokemon_tipos"
            tipo.getPokemons().forEach(pokemon -> {
                pokemon.getTipos().remove(tipo);
            });

            // Limpiamos la lista del propio tipo (por seguridad en memoria)
            tipo.getPokemons().clear();

            // Ahora sí, borramos el tipo de la base de datos
            tipoRepository.delete(tipo);
            return true;
        } else {
            return false;
        }
    }
}