package com.example.mobility.rental.adapter.out.provider.canalboat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.mobility.rental.adapter.out.provider.canalboat.client.CanalBoatStartRentalResponse;
import com.example.mobility.rental.adapter.out.provider.canalboat.client.CanalBoatClient;
import com.example.mobility.rental.adapter.out.provider.canalboat.client.CanalBoatSwapVehicleRequest;
import com.example.mobility.rental.adapter.out.provider.canalboat.client.CanalBoatSwapVehicleResponse;
import com.example.mobility.rental.application.exception.ProviderIntegrationException;
import com.example.mobility.rental.application.port.out.ProviderConfirmation;
import com.example.mobility.rental.domain.ProviderId;
import com.example.mobility.rental.domain.RenterId;
import com.example.mobility.rental.domain.VehicleId;
import com.example.mobility.rental.domain.VehicleType;
import org.junit.jupiter.api.Test;

class CanalBoatRentalAdapterTest {

    @Test
    void mapsProviderResponseToApplicationTypes() {
        CanalBoatRentalAdapter adapter = new CanalBoatRentalAdapter(new CanalBoatClient() {
            @Override
            public CanalBoatStartRentalResponse startRental(
                    com.example.mobility.rental.adapter.out.provider.canalboat.client.CanalBoatStartRentalRequest request) {
                return new CanalBoatStartRentalResponse("canal-boat-rental-boat-7", "boat-7");
            }

            @Override
            public CanalBoatSwapVehicleResponse swapVehicle(CanalBoatSwapVehicleRequest request) {
                return new CanalBoatSwapVehicleResponse(request.vesselCode());
            }
        });

        ProviderConfirmation confirmation = adapter.startRental(new RenterId("anna"), new VehicleId("boat-7"));

        assertThat(adapter.providerId()).isEqualTo(new ProviderId("canal-boat"));
        assertThat(confirmation.externalRentalId()).isEqualTo("canal-boat-rental-boat-7");
        assertThat(confirmation.vehicleId()).isEqualTo(new VehicleId("boat-7"));
        assertThat(confirmation.vehicleType()).isEqualTo(VehicleType.BOAT);
    }

    @Test
    void translatesClientFailureToApplicationException() {
        CanalBoatRentalAdapter adapter = new CanalBoatRentalAdapter(new CanalBoatClient() {
            @Override
            public CanalBoatStartRentalResponse startRental(
                    com.example.mobility.rental.adapter.out.provider.canalboat.client.CanalBoatStartRentalRequest request) {
                throw new IllegalStateException("lockout");
            }

            @Override
            public CanalBoatSwapVehicleResponse swapVehicle(CanalBoatSwapVehicleRequest request) {
                throw new IllegalStateException("lockout");
            }
        });

        assertThatThrownBy(() -> adapter.startRental(new RenterId("anna"), new VehicleId("boat-7")))
                .isInstanceOf(ProviderIntegrationException.class)
                .hasMessageContaining("Unable to start canal boat rental");
    }

    @Test
    void mapsVehicleSwapConfirmation() {
        CanalBoatRentalAdapter adapter = new CanalBoatRentalAdapter(new CanalBoatClient() {
            @Override
            public CanalBoatStartRentalResponse startRental(
                    com.example.mobility.rental.adapter.out.provider.canalboat.client.CanalBoatStartRentalRequest request) {
                throw new UnsupportedOperationException();
            }

            @Override
            public CanalBoatSwapVehicleResponse swapVehicle(CanalBoatSwapVehicleRequest request) {
                assertThat(request.externalRentalId()).isEqualTo("external-1");
                return new CanalBoatSwapVehicleResponse("boat-8");
            }
        });

        assertThat(adapter.swapVehicle("external-1", new VehicleId("boat-8")))
                .isEqualTo(new VehicleId("boat-8"));
    }
}
