package com.example.demo.exceptions;

public class ManyFiltersExceptions extends Exception {

    public ManyFiltersExceptions() {
        super("Se ingresaron más filtros de los esperados");
    }
}
