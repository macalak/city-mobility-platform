package com.example.mobility.rental.domain;

public record RentalId(String value) {
    public RentalId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Rental ID is required");
        }
    }
}
