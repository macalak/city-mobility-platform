package com.example.mobility.rental.application.exception;

public class RenterAlreadyHasActiveRental extends RuntimeException {
    public RenterAlreadyHasActiveRental(String message) {
        super(message);
    }
}
