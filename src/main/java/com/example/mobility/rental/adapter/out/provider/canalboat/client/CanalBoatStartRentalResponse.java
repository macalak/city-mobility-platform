package com.example.mobility.rental.adapter.out.provider.canalboat.client;

public record CanalBoatStartRentalResponse(String externalRentalId, String vesselCode) {
    public CanalBoatStartRentalResponse {
        if (externalRentalId == null || externalRentalId.isBlank()) {
            throw new IllegalArgumentException("External rental ID is required");
        }
        if (vesselCode == null || vesselCode.isBlank()) {
            throw new IllegalArgumentException("Vessel code is required");
        }
    }
}
