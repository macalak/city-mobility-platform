package com.example.mobility.rental.adapter.out.provider.citybike;

import com.example.mobility.rental.adapter.out.provider.citybike.client.CityBikeClient;
import com.example.mobility.rental.adapter.out.provider.citybike.client.CityBikeStartRentalRequest;
import com.example.mobility.rental.adapter.out.provider.citybike.client.CityBikeStartRentalResponse;
import com.example.mobility.rental.adapter.out.provider.citybike.client.CityBikeSwapVehicleRequest;
import com.example.mobility.rental.adapter.out.provider.citybike.client.CityBikeSwapVehicleResponse;
import com.example.mobility.rental.application.exception.ProviderIntegrationException;
import com.example.mobility.rental.application.port.out.ProviderConfirmation;
import com.example.mobility.rental.application.port.out.RentalProviderPort;
import com.example.mobility.rental.domain.ProviderId;
import com.example.mobility.rental.domain.RenterId;
import com.example.mobility.rental.domain.VehicleId;
import com.example.mobility.rental.domain.VehicleType;

public class CityBikeRentalAdapter implements RentalProviderPort {
    private final CityBikeClient client;

    public CityBikeRentalAdapter(CityBikeClient client) {
        this.client = client;
    }

    @Override
    public ProviderId providerId() {
        return new ProviderId("city-bike");
    }

    @Override
    public ProviderConfirmation startRental(RenterId renterId, VehicleId vehicleId) {
        try {
            CityBikeStartRentalRequest request = new CityBikeStartRentalRequest(renterId.value(), vehicleId.value());
            CityBikeStartRentalResponse response = client.startRental(request);
            return new ProviderConfirmation(response.externalRentalId(), new VehicleId(response.bikeId()), VehicleType.BICYCLE);
        } catch (RuntimeException ex) {
            throw new ProviderIntegrationException("Unable to start city bike rental", ex);
        }
    }

    @Override
    public VehicleId swapVehicle(String externalRentalId, VehicleId replacementVehicleId) {
        try {
            CityBikeSwapVehicleResponse response = client.swapVehicle(
                    new CityBikeSwapVehicleRequest(externalRentalId, replacementVehicleId.value()));
            return new VehicleId(response.bikeIdentifier());
        } catch (RuntimeException ex) {
            throw new ProviderIntegrationException("Unable to swap city bike rental vehicle", ex);
        }
    }
}
