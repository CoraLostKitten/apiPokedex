package com.pokedex.apipokedex.entities;
//paquetes importados
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

//Getters Setters y Constructores
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

//Indicar que es un entidad
@Entity
//nombre de la tabla
@Table(name = "Entrenadores")
public class Entrenador {
    //atributos de la clase (campos tabla)
    //PK
    @Id
    //valor numerico auto incremental
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //para que sea obligatorio (recomendable para String)/Usar NotNull otros
    @NotBlank(message = "El nombre es obligatorio") //nota para no dejarlo en blanco
    @Size(min = 3, message = "El nombre debe de tener al menos tres letras")//nota para q el tamaño sea el que queremos
    private String nombre;
    private String ciudad;
    /*Relaciones || mappedBy nombre de la propiedad en la otra clase || uso de persist para guardar los datos
    de la entidad hija cuando  se guarden las del padre*/
    @OneToMany(mappedBy = "entrenador", cascade = CascadeType.PERSIST)
    @JsonIgnoreProperties("entrenador")
    private List<Pokemon> equipo = new ArrayList<>();//nombre de la lista es equipo y lo inicializamos

}
