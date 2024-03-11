package com.polezhaiev.carsharingapp.exception;

public class LackOfCarsException extends RuntimeException {
    public LackOfCarsException(String message) {
        super(message);
    }
}
