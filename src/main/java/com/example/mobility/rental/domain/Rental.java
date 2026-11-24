package com.example.mobility.rental.domain;

import java.time.Instant;
import java.util.Objects;

/**
 * Holds rental lifecycle rules independently of Spring and provider APIs,
 * including the conditions for completing a rental.
 */
public class Rental {
    private final RentalId rentalId;
    private final RenterId renterId;
    private final ProviderId providerId;
    private final VehicleId vehicleId;
    private final VehicleType vehicleType;
    private final String externalRentalId;
    private final Instant startedAt;
    private Instant vehicleLastSwappedAt;
    private RentalStatus status;
    private Instant completedAt;

    public Rental(
            RentalId rentalId,
            RenterId renterId,
            ProviderId providerId,
            VehicleId vehicleId,
            VehicleType vehicleType,
            String externalRentalId,
            Instant startedAt) {
        this.rentalId = Objects.requireNonNull(rentalId, "Rental ID is required");
        this.renterId = Objects.requireNonNull(renterId, "Renter ID is required");
        this.providerId = Objects.requireNonNull(providerId, "Provider ID is required");
        this.vehicleId = Objects.requireNonNull(vehicleId, "Vehicle ID is required");
        this.vehicleType = Objects.requireNonNull(vehicleType, "Vehicle type is required");
        if (externalRentalId == null || externalRentalId.isBlank()) {
            throw new IllegalArgumentException("External rental ID is required");
        }
        this.externalRentalId = externalRentalId;
        this.startedAt = Objects.requireNonNull(startedAt, "Start timestamp is required");
        this.vehicleLastSwappedAt = null;
        this.status = RentalStatus.ACTIVE;
    }

    public static Rental start(
            RentalId rentalId,
            RenterId renterId,
            ProviderId providerId,
            VehicleId vehicleId,
            VehicleType vehicleType,
            String externalRentalId,
            Instant startedAt) {
        return new Rental(rentalId, renterId, providerId, vehicleId, vehicleType, externalRentalId, startedAt);
    }

    public static Rental reconstitute(
            RentalId rentalId,
            RenterId renterId,
            ProviderId providerId,
            VehicleId vehicleId,
            VehicleType vehicleType,
            String externalRentalId,
            Instant startedAt,
            RentalStatus status,
            Instant completedAt,
            Instant vehicleLastSwappedAt) {
        Rental rental = new Rental(rentalId, renterId, providerId, vehicleId, vehicleType, externalRentalId, startedAt);
        rental.status = Objects.requireNonNull(status, "Rental status is required");
        if (status == RentalStatus.ACTIVE && completedAt != null) {
            throw new IllegalArgumentException("An active rental cannot have a completion time");
        }
        if (status == RentalStatus.COMPLETED) {
            validateCompletionTime(startedAt, completedAt);
        }
        rental.completedAt = completedAt;
        if (vehicleLastSwappedAt != null && vehicleLastSwappedAt.isBefore(startedAt)) {
            throw new IllegalArgumentException("Vehicle swap time cannot precede rental start");
        }
        if (vehicleLastSwappedAt != null && completedAt != null && vehicleLastSwappedAt.isAfter(completedAt)) {
            throw new IllegalArgumentException("Vehicle swap time cannot follow rental completion");
        }
        rental.vehicleLastSwappedAt = vehicleLastSwappedAt;
        return rental;
    }

    public void validateVehicleSwap(VehicleId replacementVehicleId, Instant swapTime) {
        if (status != RentalStatus.ACTIVE) {
            throw new IllegalStateException("Only an active rental can swap vehicles");
        }
        Objects.requireNonNull(replacementVehicleId, "Replacement vehicle ID is required");
        if (vehicleId.equals(replacementVehicleId)) {
            throw new IllegalArgumentException("Replacement vehicle must differ from the current vehicle");
        }
        if (swapTime == null || swapTime.isBefore(startedAt)) {
            throw new IllegalArgumentException("Vehicle swap time cannot precede rental start");
        }
        if (vehicleLastSwappedAt != null && swapTime.isBefore(vehicleLastSwappedAt)) {
            throw new IllegalArgumentException("Vehicle swap time cannot precede the previous swap");
        }
    }

    public Rental swapVehicle(VehicleId replacementVehicleId, Instant swapTime) {
        validateVehicleSwap(replacementVehicleId, swapTime);
        return reconstitute(
                rentalId,
                renterId,
                providerId,
                replacementVehicleId,
                vehicleType,
                externalRentalId,
                startedAt,
                status,
                completedAt,
                swapTime);
    }

    public void complete(Instant completionTime) {
        if (this.status != RentalStatus.ACTIVE) {
            throw new IllegalStateException("Only an active rental can be completed");
        }
        validateCompletionTime(this.startedAt, completionTime);
        if (vehicleLastSwappedAt != null && completionTime.isBefore(vehicleLastSwappedAt)) {
            throw new IllegalArgumentException("Completion time cannot precede the last vehicle swap");
        }
        this.status = RentalStatus.COMPLETED;
        this.completedAt = completionTime;
    }

    private static void validateCompletionTime(Instant startedAt, Instant completionTime) {
        if (completionTime == null || completionTime.isBefore(startedAt)) {
            throw new IllegalArgumentException("Completion time cannot precede start time");
        }
    }

    public RentalId rentalId() {
        return rentalId;
    }

    public RenterId renterId() {
        return renterId;
    }

    public ProviderId providerId() {
        return providerId;
    }

    public VehicleId vehicleId() {
        return vehicleId;
    }

    public VehicleType vehicleType() {
        return vehicleType;
    }

    public String externalRentalId() {
        return externalRentalId;
    }

    public Instant startedAt() {
        return startedAt;
    }

    public RentalStatus status() {
        return status;
    }

    public Instant completedAt() {
        return completedAt;
    }

    public Instant vehicleLastSwappedAt() {
        return vehicleLastSwappedAt;
    }
}
