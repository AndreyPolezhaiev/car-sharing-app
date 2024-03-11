package com.polezhaiev.carsharingapp.exception.app;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}
