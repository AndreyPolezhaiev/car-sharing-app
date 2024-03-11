package com.polezhaiev.carsharingapp.exception.app;

public class LackOfCarsException extends RuntimeException {
    public LackOfCarsException(String message) {
        super(message);
    }
}
