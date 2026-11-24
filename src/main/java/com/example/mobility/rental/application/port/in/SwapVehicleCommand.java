package com.example.mobility.rental.application.port.in;

import com.example.mobility.rental.domain.RentalId;
import com.example.mobility.rental.domain.VehicleId;

public record SwapVehicleCommand(RentalId rentalId, VehicleId replacementVehicleId) {
    public SwapVehicleCommand {
        if (rentalId == null) {
            throw new IllegalArgumentException("Rental ID is required");
        }
        if (replacementVehicleId == null) {
            throw new IllegalArgumentException("Replacement vehicle ID is required");
        }
    }
}