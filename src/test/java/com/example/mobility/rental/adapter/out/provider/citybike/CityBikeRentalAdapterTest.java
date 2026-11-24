package com.example.mobility.rental.adapter.out.provider.citybike;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.mobility.rental.adapter.out.provider.citybike.client.CityBikeStartRentalResponse;
import com.example.mobility.rental.adapter.out.provider.citybike.client.CityBikeClient;
import com.example.mobility.rental.adapter.out.provider.citybike.client.CityBikeSwapVehicleRequest;
import com.example.mobility.rental.adapter.out.provider.citybike.client.CityBikeSwapVehicleResponse;
import com.example.mobility.rental.application.exception.ProviderIntegrationException;
import com.example.mobility.rental.application.port.out.ProviderConfirmation;
import com.example.mobility.rental.domain.ProviderId;
import com.example.mobility.rental.domain.RenterId;
import com.example.mobility.rental.domain.VehicleId;
import com.example.mobility.rental.domain.VehicleType;
import org.junit.jupiter.api.Test;

class CityBikeRentalAdapterTest {

    @Test
    void mapsProviderResponseToApplicationTypes() {
        CityBikeRentalAdapter adapter = new CityBikeRentalAdapter(new CityBikeClient() {
            @Override
            public CityBikeStartRentalResponse startRental(
                    com.example.mobility.rental.adapter.out.provider.citybike.client.CityBikeStartRentalRequest request) {
                return new CityBikeStartRentalResponse("city-bike-rental-bike-42", "bike-42");
            }

            @Override
            public CityBikeSwapVehicleResponse swapVehicle(CityBikeSwapVehicleRequest request) {
                return new CityBikeSwapVehicleResponse(request.bikeIdentifier());
            }
        });

        ProviderConfirmation confirmation = adapter.startRental(new RenterId("anna"), new VehicleId("bike-42"));

        assertThat(adapter.providerId()).isEqualTo(new ProviderId("city-bike"));
        assertThat(confirmation.externalRentalId()).isEqualTo("city-bike-rental-bike-42");
        assertThat(confirmation.vehicleId()).isEqualTo(new VehicleId("bike-42"));
        assertThat(confirmation.vehicleType()).isEqualTo(VehicleType.BICYCLE);
    }

    @Test
    void translatesClientFailureToApplicationException() {
        CityBikeRentalAdapter adapter = new CityBikeRentalAdapter(new CityBikeClient() {
            @Override
            public CityBikeStartRentalResponse startRental(
                    com.example.mobility.rental.adapter.out.provider.citybike.client.CityBikeStartRentalRequest request) {
                throw new IllegalStateException("cloudy");
            }

            @Override
            public CityBikeSwapVehicleResponse swapVehicle(CityBikeSwapVehicleRequest request) {
                throw new IllegalStateException("cloudy");
            }
        });

        assertThatThrownBy(() -> adapter.startRental(new RenterId("anna"), new VehicleId("bike-42")))
                .isInstanceOf(ProviderIntegrationException.class)
                .hasMessageContaining("Unable to start city bike rental");
    }

    @Test
    void mapsVehicleSwapConfirmation() {
        CityBikeRentalAdapter adapter = new CityBikeRentalAdapter(new CityBikeClient() {
            @Override
            public CityBikeStartRentalResponse startRental(
                    com.example.mobility.rental.adapter.out.provider.citybike.client.CityBikeStartRentalRequest request) {
                throw new UnsupportedOperationException();
            }

            @Override
            public CityBikeSwapVehicleResponse swapVehicle(CityBikeSwapVehicleRequest request) {
                assertThat(request.externalRentalId()).isEqualTo("external-1");
                return new CityBikeSwapVehicleResponse("bike-43");
            }
        });

        assertThat(adapter.swapVehicle("external-1", new VehicleId("bike-43")))
                .isEqualTo(new VehicleId("bike-43"));
    }
}
