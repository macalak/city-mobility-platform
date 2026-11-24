package com.example.mobility.rental.domain;

public record ProviderId(String value) {
    public ProviderId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Provider ID is required");
        }
    }
}
