package com.gestion.evenements.model.evenements.exceptions;
public class CapaciteDepasseeException extends EvenementException
{
    public CapaciteDepasseeException(String message) {
        super(message);
    }
    public CapaciteDepasseeException(String message, Throwable
            cause) {
        super(message, cause);
    }
}
