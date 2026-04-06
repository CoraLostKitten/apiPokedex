package com.pokedex.apipokedex.exception;
//paquetes importados
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

//anotacionn que le dice a la clase que esto va a ser un controlador de errores
@RestControllerAdvice
public class GlobalExceptionHadler {
    // Manejo de errores de validación (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errores.put(error.getField(),error.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errores);
    }

    /*Manejar errores globalmente con @ControllerAdvice y @ExceptionHandler de forma que si no se introducen los campos obligatorios se devuelva información sobre los errores de validación.*/
    // Manejo de excepciones generales (Errores inesperados)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,String>> handleGlobalException(Exception ex) {
        ex.printStackTrace();   //Para ver toda la traza del error en la consola
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("Error",ex.toString()));
    }
}
