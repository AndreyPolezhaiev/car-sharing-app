package com.polezhaiev.carsharingapp.exception.app;

public class RentalIsNotActiveException extends RuntimeException {
    public RentalIsNotActiveException(String message) {
        super(message);
    }
}
