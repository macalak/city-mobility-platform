package com.example.mobility.layeredrental;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.mobility.layeredrental.datasource.persistence.LayeredRentalRepository;
import com.example.mobility.layeredrental.domain.LayeredRental;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
class LayeredRentalRepositoryTest {
    @Autowired
    private LayeredRentalRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void persistsRentalAndFindsActiveRentals() {
        Instant startedAt = Instant.parse("2024-01-01T10:00:00Z");
        repository.save(new LayeredRental("rental-1", "anna", "city-bike", "bike-42",
                "BICYCLE", "external-1", startedAt, "ACTIVE"));
        entityManager.flush();
        entityManager.clear();

        assertThat(repository.existsByRenterIdAndStatus("anna", "ACTIVE")).isTrue();
        assertThat(repository.existsByRenterIdAndStatus("anna", "COMPLETED")).isFalse();
        assertThat(repository.existsByRenterIdAndStatus("bob", "ACTIVE")).isFalse();
        assertThat(repository.findById("rental-1")).get().satisfies(rental -> {
            assertThat(rental.renterId()).isEqualTo("anna");
            assertThat(rental.providerId()).isEqualTo("city-bike");
            assertThat(rental.vehicleId()).isEqualTo("bike-42");
            assertThat(rental.vehicleType()).isEqualTo("BICYCLE");
            assertThat(rental.externalRentalId()).isEqualTo("external-1");
            assertThat(rental.startedAt()).isEqualTo(startedAt);
            assertThat(rental.status()).isEqualTo("ACTIVE");
        });
    }
}