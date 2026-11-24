package com.example.mobility.rental.application.port.in;

import com.example.mobility.rental.domain.ProviderId;
import com.example.mobility.rental.domain.RenterId;
import com.example.mobility.rental.domain.VehicleId;

public record StartRentalCommand(RenterId renterId, ProviderId providerId, VehicleId vehicleId) {
    public StartRentalCommand {
        if (renterId == null) {
            throw new IllegalArgumentException("Renter ID is required");
        }
        if (providerId == null) {
            throw new IllegalArgumentException("Provider ID is required");
        }
        if (vehicleId == null) {
            throw new IllegalArgumentException("Vehicle ID is required");
        }
    }
}