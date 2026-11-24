package com.example.mobility.rental.adapter.out.provider.citybike.client;

public record CityBikeStartRentalResponse(String externalRentalId, String bikeId) {
    public CityBikeStartRentalResponse {
        if (externalRentalId == null || externalRentalId.isBlank()) {
            throw new IllegalArgumentException("External rental ID is required");
        }
        if (bikeId == null || bikeId.isBlank()) {
            throw new IllegalArgumentException("Bike ID is required");
        }
    }
}
