package com.example.mobility.rental.application.port.out;

import com.example.mobility.rental.domain.VehicleId;
import com.example.mobility.rental.domain.VehicleType;

public record ProviderConfirmation(
        String externalRentalId,
        VehicleId vehicleId,
        VehicleType vehicleType) {
    public ProviderConfirmation {
        if (externalRentalId == null || externalRentalId.isBlank()) {
            throw new IllegalArgumentException("External rental ID is required");
        }
        if (vehicleId == null) {
            throw new IllegalArgumentException("Vehicle ID is required");
        }
        if (vehicleType == null) {
            throw new IllegalArgumentException("Vehicle type is required");
        }
    }
}
