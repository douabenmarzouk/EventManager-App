package com.gestion.evenements.model.evenements.exceptions;
public class EvenementException extends Exception {
    public EvenementException(String message) {
        super(message);
    }
    public EvenementException(String message, Throwable cause) {
        super(message, cause);
    }}
