package com.polezhaiev.carsharingapp.exception;

public class RentalIsNotActiveException extends RuntimeException {
    public RentalIsNotActiveException(String message) {
        super(message);
    }
}
