package com.example.mobility.rental.adapter.out.provider.canalboat;

import com.example.mobility.rental.adapter.out.provider.canalboat.client.CanalBoatClient;
import com.example.mobility.rental.adapter.out.provider.canalboat.client.CanalBoatStartRentalRequest;
import com.example.mobility.rental.adapter.out.provider.canalboat.client.CanalBoatStartRentalResponse;
import com.example.mobility.rental.adapter.out.provider.canalboat.client.CanalBoatSwapVehicleRequest;
import com.example.mobility.rental.adapter.out.provider.canalboat.client.CanalBoatSwapVehicleResponse;
import com.example.mobility.rental.application.exception.ProviderIntegrationException;
import com.example.mobility.rental.application.port.out.ProviderConfirmation;
import com.example.mobility.rental.application.port.out.RentalProviderPort;
import com.example.mobility.rental.domain.ProviderId;
import com.example.mobility.rental.domain.RenterId;
import com.example.mobility.rental.domain.VehicleId;
import com.example.mobility.rental.domain.VehicleType;

public class CanalBoatRentalAdapter implements RentalProviderPort {
    private final CanalBoatClient client;

    public CanalBoatRentalAdapter(CanalBoatClient client) {
        this.client = client;
    }

    @Override
    public ProviderId providerId() {
        return new ProviderId("canal-boat");
    }

    @Override
    public ProviderConfirmation startRental(RenterId renterId, VehicleId vehicleId) {
        try {
            CanalBoatStartRentalRequest request = new CanalBoatStartRentalRequest(renterId.value(), vehicleId.value());
            CanalBoatStartRentalResponse response = client.startRental(request);
            return new ProviderConfirmation(response.externalRentalId(), new VehicleId(response.vesselCode()), VehicleType.BOAT);
        } catch (RuntimeException ex) {
            throw new ProviderIntegrationException("Unable to start canal boat rental", ex);
        }
    }

    @Override
    public VehicleId swapVehicle(String externalRentalId, VehicleId replacementVehicleId) {
        try {
            CanalBoatSwapVehicleResponse response = client.swapVehicle(
                    new CanalBoatSwapVehicleRequest(externalRentalId, replacementVehicleId.value()));
            return new VehicleId(response.vesselCode());
        } catch (RuntimeException ex) {
            throw new ProviderIntegrationException("Unable to swap canal boat rental vehicle", ex);
        }
    }
}
