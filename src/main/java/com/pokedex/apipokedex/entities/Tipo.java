package com.pokedex.apipokedex.entities;
//paquetes importados
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

//Getters Stters y Constructores
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

//Indicar que es un entidad
@Entity
//nommbre de la tabla
@Table(name = "Tipos")
public class Tipo {
    //atributos de la clase (campos tabla)
    //PK
    @Id
    //valor numerico auto incremental
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    //Relaciones
    @ManyToMany(mappedBy = "tipos") // "Ya está todo configurado en la lista 'tipos' de la clase Pokemon"
    private List<Pokemon> pokemons;
}
