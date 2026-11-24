package com.example.mobility.rental.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.mobility.rental.adapter.out.persistence.InMemoryRentalRepository;
import com.example.mobility.rental.application.exception.RenterAlreadyHasActiveRental;
import com.example.mobility.rental.application.exception.UnknownRentalProvider;
import com.example.mobility.rental.application.port.in.StartRentalCommand;
import com.example.mobility.rental.application.port.in.SwapVehicleCommand;
import com.example.mobility.rental.application.port.out.ProviderConfirmation;
import com.example.mobility.rental.application.port.out.RentalProviderPort;
import com.example.mobility.rental.application.service.RentalApplicationService;
import com.example.mobility.rental.application.service.RentalProviderRegistry;
import com.example.mobility.rental.domain.ProviderId;
import com.example.mobility.rental.domain.RentalId;
import com.example.mobility.rental.domain.RenterId;
import com.example.mobility.rental.domain.VehicleId;
import com.example.mobility.rental.domain.VehicleType;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class RentalApplicationServiceTest {

    @Test
    void startsRentalThroughSelectedProvider() {
        RentalProviderPort provider = new RentalProviderPort() {
            @Override
            public ProviderId providerId() {
                return new ProviderId("city-bike");
            }

            @Override
            public ProviderConfirmation startRental(RenterId renterId, VehicleId vehicleId) {
                return new ProviderConfirmation("external-1", new VehicleId("bike-42"), VehicleType.BICYCLE);
            }

            @Override
            public VehicleId swapVehicle(String externalRentalId, VehicleId replacementVehicleId) {
                return replacementVehicleId;
            }
        };

        RentalApplicationService service = new RentalApplicationService(
                new RentalProviderRegistry(List.of(provider)),
                new InMemoryRentalRepository(),
                () -> Instant.parse("2024-01-01T10:00:00Z"));

        RentalId rentalId = service.startRental(new StartRentalCommand(
                new RenterId("anna"),
                new ProviderId("city-bike"),
                new VehicleId("bike-42")));

        assertThat(rentalId).isNotNull();
    }

    @Test
    void rejectsRenterWithActiveRental() {
        InMemoryRentalRepository repository = new InMemoryRentalRepository();
        RentalProviderPort provider = new RentalProviderPort() {
            @Override
            public ProviderId providerId() {
                return new ProviderId("city-bike");
            }

            @Override
            public ProviderConfirmation startRental(RenterId renterId, VehicleId vehicleId) {
                return new ProviderConfirmation("external-1", new VehicleId("bike-42"), VehicleType.BICYCLE);
            }

            @Override
            public VehicleId swapVehicle(String externalRentalId, VehicleId replacementVehicleId) {
                return replacementVehicleId;
            }
        };

        RentalApplicationService service = new RentalApplicationService(
                new RentalProviderRegistry(List.of(provider)),
                repository,
                () -> Instant.parse("2024-01-01T10:00:00Z"));

        repository.save(com.example.mobility.rental.domain.Rental.start(
                new RentalId("existing"),
                new RenterId("anna"),
                new ProviderId("city-bike"),
                new VehicleId("bike-99"),
                VehicleType.BICYCLE,
                "external-existing",
                Instant.parse("2024-01-01T09:00:00Z")));

        assertThatThrownBy(() -> service.startRental(new StartRentalCommand(
                new RenterId("anna"),
                new ProviderId("city-bike"),
                new VehicleId("bike-42"))))
                .isInstanceOf(RenterAlreadyHasActiveRental.class);
    }

    @Test
    void rejectsUnknownProvider() {
        RentalApplicationService service = new RentalApplicationService(
                new RentalProviderRegistry(List.of()),
                new InMemoryRentalRepository(),
                () -> Instant.parse("2024-01-01T10:00:00Z"));

        assertThatThrownBy(() -> service.startRental(new StartRentalCommand(
                new RenterId("anna"),
                new ProviderId("missing-provider"),
                new VehicleId("bike-42"))))
                .isInstanceOf(UnknownRentalProvider.class);
    }

    @Test
    void swapsVehicleThroughProviderAndSavesUpdatedRental() {
        InMemoryRentalRepository repository = new InMemoryRentalRepository();
        RentalProviderPort provider = new RentalProviderPort() {
            @Override
            public ProviderId providerId() {
                return new ProviderId("city-bike");
            }

            @Override
            public ProviderConfirmation startRental(RenterId renterId, VehicleId vehicleId) {
                return new ProviderConfirmation("external-1", vehicleId, VehicleType.BICYCLE);
            }

            @Override
            public VehicleId swapVehicle(String externalRentalId, VehicleId replacementVehicleId) {
                assertThat(externalRentalId).isEqualTo("external-1");
                return replacementVehicleId;
            }
        };
        RentalId rentalId = new RentalId("r-1");
        repository.save(com.example.mobility.rental.domain.Rental.start(
                rentalId,
                new RenterId("anna"),
                new ProviderId("city-bike"),
                new VehicleId("bike-42"),
                VehicleType.BICYCLE,
                "external-1",
                Instant.parse("2024-01-01T10:00:00Z")));
        RentalApplicationService service = new RentalApplicationService(
                new RentalProviderRegistry(List.of(provider)),
                repository,
                () -> Instant.parse("2024-01-01T10:30:00Z"));

        var swapped = service.swapVehicle(new SwapVehicleCommand(rentalId, new VehicleId("bike-43")));

        assertThat(swapped.vehicleId()).isEqualTo(new VehicleId("bike-43"));
        assertThat(repository.findById(rentalId)).get().extracting(rental -> rental.vehicleId())
                .isEqualTo(new VehicleId("bike-43"));
    }
}
