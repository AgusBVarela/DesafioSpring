package com.example.demo.exceptions;

public class IncompleteDataClientException extends Exception{
    public IncompleteDataClientException() {
        super("El cliente que se intenta incorporar posee datos incompletos.");
    }
}
