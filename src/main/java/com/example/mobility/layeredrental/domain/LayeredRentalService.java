package com.example.mobility.layeredrental.domain;

import com.example.mobility.layeredrental.datasource.integration.LayeredCanalBoatClient;
import com.example.mobility.layeredrental.datasource.integration.LayeredCityBikeClient;
import com.example.mobility.layeredrental.datasource.persistence.LayeredRentalRepository;

import java.time.Instant;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 * In this layered example, the service contains the one-active-rental rule
 * alongside provider-specific calls and HTTP error details. This coupling is
 * deliberate for comparison; layered designs can also isolate business logic.
 */
@Service
public class LayeredRentalService {
    private final LayeredRentalRepository repository;
    private final LayeredCityBikeClient cityBikeClient;
    private final LayeredCanalBoatClient canalBoatClient;
    // TO-DO: Define LayeredGreenScooterClient
    //private final LayeredGreenScooterClient greenScooterClient;

    public LayeredRentalService(LayeredRentalRepository repository, LayeredCityBikeClient cityBikeClient,
            LayeredCanalBoatClient canalBoatClient) {
        this.repository = repository;
        this.cityBikeClient = cityBikeClient;
        this.canalBoatClient = canalBoatClient;
    }

    public LayeredRental startRental(String renterId, String providerId, String vehicleId) {
        requireValue(renterId, "Renter ID");
        requireValue(providerId, "Provider ID");
        requireValue(vehicleId, "Vehicle ID");

        if (repository.existsByRenterIdAndStatus(renterId, "ACTIVE")) {
            throw new LayeredRentalException(HttpStatus.CONFLICT, "ACTIVE_RENTAL_EXISTS",
                    "Renter already has an active rental: " + renterId);
        }

        String externalRentalId;
        String confirmedVehicleId;
        String vehicleType;
        try {
            switch (providerId) {
                case "city-bike" -> {
                    var response = cityBikeClient.startRental(new LayeredCityBikeClient.Request(renterId, vehicleId));
                    externalRentalId = response.externalRentalId();
                    confirmedVehicleId = response.bikeId();
                    vehicleType = "BICYCLE";
                }
                case "canal-boat" -> {
                    var response = canalBoatClient.startRental(new LayeredCanalBoatClient.Request(renterId, vehicleId));
                    externalRentalId = response.externalRentalId();
                    confirmedVehicleId = response.vesselCode();
                    vehicleType = "BOAT";
                }
                // TO-DO: Add green-scooter branch
                // case "green-scooter" -> {
                //     var response = greenScooterClient.startRental(new LayeredGreenScooterClient.Request(renterId, vehicleId));
                //     externalRentalId = response.externalRentalId();
                //     confirmedVehicleId = response.scooterId();
                //     vehicleType = "SCOOTER";
                // }
                default -> throw new LayeredRentalException(HttpStatus.NOT_FOUND, "UNKNOWN_RENTAL_PROVIDER",
                        "Unknown rental provider: " + providerId);
            }
        } catch (IllegalStateException exception) {
            throw new LayeredRentalException(HttpStatus.BAD_GATEWAY, "PROVIDER_UNAVAILABLE",
                    "Unable to start rental with provider: " + providerId);
        }

        LayeredRental rental = new LayeredRental(UUID.randomUUID().toString(), renterId, providerId,
                confirmedVehicleId, vehicleType, externalRentalId, Instant.now(), "ACTIVE");
        repository.save(rental);
        return rental;
    }

    private void requireValue(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new LayeredRentalException(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", name + " is required");
        }
    }
}