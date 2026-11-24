package com.example.mobility.rental.domain;

public record RenterId(String value) {
    public RenterId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Renter ID is required");
        }
    }
}
