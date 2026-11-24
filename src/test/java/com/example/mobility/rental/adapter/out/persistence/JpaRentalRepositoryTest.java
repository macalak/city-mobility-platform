package com.example.mobility.rental.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.mobility.rental.application.port.out.RentalRepository;
import com.example.mobility.rental.domain.ProviderId;
import com.example.mobility.rental.domain.Rental;
import com.example.mobility.rental.domain.RentalId;
import com.example.mobility.rental.domain.RentalStatus;
import com.example.mobility.rental.domain.RenterId;
import com.example.mobility.rental.domain.VehicleId;
import com.example.mobility.rental.domain.VehicleType;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("jpa")
@Import(JpaRentalRepository.class)
class JpaRentalRepositoryTest {
    @Autowired
    private RentalRepository rentalRepository;

    @Test
    void savesAndLoadsRentalWithoutLeakingPersistenceEntity() {
        Rental rental = Rental.start(
                new RentalId("rental-1"),
                new RenterId("anna"),
                new ProviderId("city-bike"),
                new VehicleId("bike-42"),
                VehicleType.BICYCLE,
                "external-1",
                Instant.parse("2026-11-16T10:00:00Z"));

        rentalRepository.save(rental);

        assertThat(rentalRepository.hasActiveRentalFor(new RenterId("anna"))).isTrue();
        assertThat(rentalRepository.findById(new RentalId("rental-1")))
                .get()
                .satisfies(loaded -> {
                    assertThat(loaded.renterId()).isEqualTo(new RenterId("anna"));
                    assertThat(loaded.vehicleId()).isEqualTo(new VehicleId("bike-42"));
                    assertThat(loaded.status()).isEqualTo(RentalStatus.ACTIVE);
                });
    }

    @Test
    void restoresCompletedRentalState() {
        Rental rental = Rental.start(
                new RentalId("completed-rental"),
                new RenterId("anna"),
                new ProviderId("city-bike"),
                new VehicleId("bike-42"),
                VehicleType.BICYCLE,
                "external-2",
                Instant.parse("2026-11-16T10:00:00Z"));
        rental.complete(Instant.parse("2026-11-16T11:00:00Z"));

        rentalRepository.save(rental);

        assertThat(rentalRepository.hasActiveRentalFor(new RenterId("anna"))).isFalse();
        assertThat(rentalRepository.findById(new RentalId("completed-rental")))
                .get()
                .satisfies(loaded -> {
                    assertThat(loaded.status()).isEqualTo(RentalStatus.COMPLETED);
                    assertThat(loaded.completedAt()).isEqualTo(Instant.parse("2026-11-16T11:00:00Z"));
                });
    }

    @Test
    void persistsVehicleSwapAndItsTimestamp() {
        Rental rental = Rental.start(
                new RentalId("swapped-rental"),
                new RenterId("anna"),
                new ProviderId("city-bike"),
                new VehicleId("bike-42"),
                VehicleType.BICYCLE,
                "external-3",
                Instant.parse("2026-11-16T10:00:00Z"));
        Rental swapped = rental.swapVehicle(
                new VehicleId("bike-43"),
                Instant.parse("2026-11-16T10:30:00Z"));

        rentalRepository.save(swapped);

        assertThat(rentalRepository.findById(new RentalId("swapped-rental")))
                .get()
                .satisfies(loaded -> {
                    assertThat(loaded.vehicleId()).isEqualTo(new VehicleId("bike-43"));
                    assertThat(loaded.vehicleLastSwappedAt()).isEqualTo(Instant.parse("2026-11-16T10:30:00Z"));
                });
    }
}