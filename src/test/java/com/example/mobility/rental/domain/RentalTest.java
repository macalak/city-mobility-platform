package com.example.mobility.rental.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class RentalTest {

    @Test
    void startsAsActive() {
        Rental rental = Rental.start(
                new RentalId("r-1"),
                new RenterId("anna"),
                new ProviderId("city-bike"),
                new VehicleId("bike-42"),
                VehicleType.BICYCLE,
                "external-rental-1",
                Instant.parse("2024-01-01T10:00:00Z"));

        assertThat(rental.status()).isEqualTo(RentalStatus.ACTIVE);
        assertThat(rental.completedAt()).isNull();
    }

    @Test
    void completesOnlyOnce() {
        Rental rental = Rental.start(
                new RentalId("r-1"),
                new RenterId("anna"),
                new ProviderId("city-bike"),
                new VehicleId("bike-42"),
                VehicleType.BICYCLE,
                "external-rental-1",
                Instant.parse("2024-01-01T10:00:00Z"));

        rental.complete(Instant.parse("2024-01-01T11:00:00Z"));

        assertThatThrownBy(() -> rental.complete(Instant.parse("2024-01-01T12:00:00Z")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Only an active rental can be completed");
    }

    @Test
    void completionCannotPrecedeStart() {
        Rental rental = Rental.start(
                new RentalId("r-1"),
                new RenterId("anna"),
                new ProviderId("city-bike"),
                new VehicleId("bike-42"),
                VehicleType.BICYCLE,
                "external-rental-1",
                Instant.parse("2024-01-01T10:00:00Z"));

        assertThatThrownBy(() -> rental.complete(Instant.parse("2024-01-01T09:59:00Z")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Completion time cannot precede start time");
    }

    @Test
    void swapsVehicleOnActiveRentalAndLeavesOriginalUnchanged() {
        Rental rental = Rental.start(
                new RentalId("r-1"),
                new RenterId("anna"),
                new ProviderId("city-bike"),
                new VehicleId("bike-42"),
                VehicleType.BICYCLE,
                "external-rental-1",
                Instant.parse("2024-01-01T10:00:00Z"));

        Rental swapped = rental.swapVehicle(
                new VehicleId("bike-43"),
                Instant.parse("2024-01-01T10:30:00Z"));

        assertThat(rental.vehicleId()).isEqualTo(new VehicleId("bike-42"));
        assertThat(swapped.vehicleId()).isEqualTo(new VehicleId("bike-43"));
        assertThat(swapped.vehicleType()).isEqualTo(VehicleType.BICYCLE);
        assertThat(swapped.vehicleLastSwappedAt()).isEqualTo(Instant.parse("2024-01-01T10:30:00Z"));
    }

    @Test
    void rejectsSwapToSameVehicleOrBeforeRentalStart() {
        Rental rental = Rental.start(
                new RentalId("r-1"),
                new RenterId("anna"),
                new ProviderId("city-bike"),
                new VehicleId("bike-42"),
                VehicleType.BICYCLE,
                "external-rental-1",
                Instant.parse("2024-01-01T10:00:00Z"));

        assertThatThrownBy(() -> rental.swapVehicle(
                new VehicleId("bike-42"), Instant.parse("2024-01-01T10:30:00Z")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must differ");
        assertThatThrownBy(() -> rental.swapVehicle(
                new VehicleId("bike-43"), Instant.parse("2024-01-01T09:59:00Z")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot precede rental start");
    }

    @Test
    void cannotSwapVehicleAfterRentalCompletes() {
        Rental rental = Rental.start(
                new RentalId("r-1"),
                new RenterId("anna"),
                new ProviderId("city-bike"),
                new VehicleId("bike-42"),
                VehicleType.BICYCLE,
                "external-rental-1",
                Instant.parse("2024-01-01T10:00:00Z"));
        rental.complete(Instant.parse("2024-01-01T11:00:00Z"));

        assertThatThrownBy(() -> rental.swapVehicle(
                new VehicleId("bike-43"), Instant.parse("2024-01-01T10:30:00Z")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Only an active rental can swap vehicles");
    }

        @Test
        void completionCannotPrecedeLastVehicleSwap() {
                Rental rental = Rental.start(
                                                new RentalId("r-1"),
                                                new RenterId("anna"),
                                                new ProviderId("city-bike"),
                                                new VehicleId("bike-42"),
                                                VehicleType.BICYCLE,
                                                "external-rental-1",
                                                Instant.parse("2024-01-01T10:00:00Z"))
                                .swapVehicle(new VehicleId("bike-43"), Instant.parse("2024-01-01T10:30:00Z"));

                assertThatThrownBy(() -> rental.complete(Instant.parse("2024-01-01T10:20:00Z")))
                                .isInstanceOf(IllegalArgumentException.class)
                                .hasMessageContaining("cannot precede the last vehicle swap");
        }
}
