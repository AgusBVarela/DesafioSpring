package com.example.demo.exceptions;

public class ManyFiltersException extends Exception {

    public ManyFiltersException() {
        super("Se ingresaron más filtros de los esperados");
    }
}
