package com.example.mobility.rental.adapter.out.provider.canalboat.client;

public record CanalBoatStartRentalRequest(String passengerReference, String vesselCode) {
    public CanalBoatStartRentalRequest {
        if (passengerReference == null || passengerReference.isBlank()) {
            throw new IllegalArgumentException("Canal boat passenger reference is required");
        }
        if (vesselCode == null || vesselCode.isBlank()) {
            throw new IllegalArgumentException("Canal boat vessel code is required");
        }
    }
}
