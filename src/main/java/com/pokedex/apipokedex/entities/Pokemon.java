package com.pokedex.apipokedex.entities;
//paquetes importados
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

//Getters Stters y Constructores
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

//Indicar que es una entidad
@Entity
//nommbre de la tabla
@Table(name = "Pokemons")
public class Pokemon {
    //atributos de la clase (campos tabla)
    //PK
    @Id
    //valor numerico auto incremental
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //para que sea obligatorio (recomendable para String)/Usar NotNull otros
   /* @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, message = "El nombre debe de tener al menos tres letras")*/
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    //con column indicamos que el campo se cree de una forma concreta
    @Column(unique = true)
    @NotNull(message = "El número de la pokedex es obligatorio")
    private Long numPokedex;
    private String descripcion;
    private Long nivel;;
    //Relaciones entre tablas
    //Relacion con entrenador
    @ManyToOne(fetch = FetchType.EAGER)
    //columna en tabla de pokemon y es nullable porque pueden ser pokemon salvajes
    @JoinColumn(name = "entrenador_id", nullable = true)
    @JsonIgnore // <--- TRADUCCIÓN: "Cuando pintes el Pokemon, haz como que el Entrenador no existe".
    private Entrenador entrenador; //referenciamos a la otra parte y arriba construimos la relacion
    //Relacion con tipos@ManyToMany
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "pokemon_tipos", // Nombre de la tabla intermedia que se creará sola
            joinColumns = @JoinColumn(name = "pokemon_id"), // Columna para el ID del Pokemon
            inverseJoinColumns = @JoinColumn(name = "tipo_id") // Columna para el ID del Tipo
    )
    private List<Tipo> tipos = new ArrayList<>();

}
