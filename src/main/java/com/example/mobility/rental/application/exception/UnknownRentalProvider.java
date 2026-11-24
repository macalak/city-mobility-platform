package com.example.mobility.rental.application.exception;

public class UnknownRentalProvider extends RuntimeException {
    public UnknownRentalProvider(String message) {
        super(message);
    }
}
