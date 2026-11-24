package com.example.mobility.rental.adapter.in.web;

public record RentalCreatedResponse(String rentalId, String status) {
    public RentalCreatedResponse {
        if (rentalId == null || rentalId.isBlank()) {
            throw new IllegalArgumentException("Rental ID is required");
        }
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("Status is required");
        }
    }
}
