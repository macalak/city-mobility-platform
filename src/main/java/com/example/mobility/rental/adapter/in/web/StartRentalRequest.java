package com.example.mobility.rental.adapter.in.web;

import com.example.mobility.rental.application.port.in.StartRentalCommand;
import com.example.mobility.rental.domain.ProviderId;
import com.example.mobility.rental.domain.RenterId;
import com.example.mobility.rental.domain.VehicleId;

public record StartRentalRequest(String renterId, String providerId, String vehicleId) {
    public StartRentalCommand toCommand() {
        return new StartRentalCommand(new RenterId(renterId), new ProviderId(providerId), new VehicleId(vehicleId));
    }
}