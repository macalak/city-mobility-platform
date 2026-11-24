package com.example.mobility.layeredrental;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.example.mobility.layeredrental.datasource.persistence.LayeredRentalRepository;
import com.example.mobility.layeredrental.domain.LayeredRental;
import com.example.mobility.layeredrental.domain.LayeredRentalException;
import com.example.mobility.layeredrental.domain.LayeredRentalService;
import jakarta.persistence.EntityManager;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class LayeredRentalServiceTest {
    @Autowired
    private LayeredRentalService service;

    @Autowired
    private LayeredRentalRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void startsAndPersistsRentalUsingSpringDependencies() {
        LayeredRental rental = service.startRental("anna", "city-bike", "bike-42");
        repository.flush();
        entityManager.clear();

        assertThat(repository.findById(Objects.requireNonNull(rental.rentalId()))).get().satisfies(saved -> {
            assertThat(saved.renterId()).isEqualTo("anna");
            assertThat(saved.vehicleId()).isEqualTo("bike-42");
            assertThat(saved.vehicleType()).isEqualTo("BICYCLE");
            assertThat(saved.externalRentalId()).isEqualTo("city-bike-rental-bike-42");
            assertThat(saved.status()).isEqualTo("ACTIVE");
        });
    }

    @Test
    void rejectsAnotherActiveRentalForTheSameRenter() {
        service.startRental("anna", "city-bike", "bike-42");

        assertThatExceptionOfType(LayeredRentalException.class)
                .isThrownBy(() -> service.startRental("anna", "canal-boat", "boat-7"))
                .satisfies(exception -> {
                    assertThat(exception.status()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(exception.code()).isEqualTo("ACTIVE_RENTAL_EXISTS");
                });
    }

    @Test
    void translatesProviderFailure() {
        assertThatExceptionOfType(LayeredRentalException.class)
                .isThrownBy(() -> service.startRental("anna", "city-bike", "fail-city-bike"))
                .satisfies(exception -> {
                    assertThat(exception.status()).isEqualTo(HttpStatus.BAD_GATEWAY);
                    assertThat(exception.code()).isEqualTo("PROVIDER_UNAVAILABLE");
                });
        assertThat(repository.existsByRenterIdAndStatus("anna", "ACTIVE")).isFalse();
    }
}