package com.example.mobility.rental.adapter.out.provider.citybike.client;

public record CityBikeStartRentalRequest(String customerReference, String bikeIdentifier) {
    public CityBikeStartRentalRequest {
        if (customerReference == null || customerReference.isBlank()) {
            throw new IllegalArgumentException("City bike customer reference is required");
        }
        if (bikeIdentifier == null || bikeIdentifier.isBlank()) {
            throw new IllegalArgumentException("City bike identifier is required");
        }
    }
}
