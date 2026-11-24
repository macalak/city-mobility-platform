package com.example.mobility.rental.domain;

public record VehicleId(String value) {
    public VehicleId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Vehicle ID is required");
        }
    }
}
