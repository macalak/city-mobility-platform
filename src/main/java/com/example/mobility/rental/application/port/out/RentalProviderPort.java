package com.example.mobility.rental.application.port.out;

import com.example.mobility.rental.domain.ProviderId;
import com.example.mobility.rental.domain.RenterId;
import com.example.mobility.rental.domain.VehicleId;

public interface RentalProviderPort {
    ProviderId providerId();

    ProviderConfirmation startRental(RenterId renterId, VehicleId vehicleId);

    VehicleId swapVehicle(String externalRentalId, VehicleId replacementVehicleId);
}
