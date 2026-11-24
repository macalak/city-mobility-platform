package com.example.mobility.rental.adapter.out.persistence;

import com.example.mobility.rental.domain.ProviderId;
import com.example.mobility.rental.domain.Rental;
import com.example.mobility.rental.domain.RentalId;
import com.example.mobility.rental.domain.RentalStatus;
import com.example.mobility.rental.domain.RenterId;
import com.example.mobility.rental.domain.VehicleId;
import com.example.mobility.rental.domain.VehicleType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "rentals")
public class RentalEntity {
    @Id
    @Column(name = "rental_id", nullable = false, updatable = false)
    private String rentalId;

    @Column(name = "renter_id", nullable = false)
    private String renterId;

    @Column(name = "provider_id", nullable = false)
    private String providerId;

    @Column(name = "vehicle_id", nullable = false)
    private String vehicleId;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false)
    private VehicleType vehicleType;

    @Column(name = "external_rental_id", nullable = false)
    private String externalRentalId;

    @Column(name = "started_at", nullable = false)
    private Instant startedAt;

    @Column(name = "vehicle_last_swapped_at")
    private Instant vehicleLastSwappedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RentalStatus status;

    @Column(name = "completed_at")
    private Instant completedAt;

    protected RentalEntity() {}

    private RentalEntity(Rental rental) {
        rentalId = rental.rentalId().value();
        renterId = rental.renterId().value();
        providerId = rental.providerId().value();
        vehicleId = rental.vehicleId().value();
        vehicleType = rental.vehicleType();
        externalRentalId = rental.externalRentalId();
        startedAt = rental.startedAt();
        vehicleLastSwappedAt = rental.vehicleLastSwappedAt();
        status = rental.status();
        completedAt = rental.completedAt();
    }

    public static RentalEntity fromDomain(Rental rental) {
        return new RentalEntity(rental);
    }

    public Rental toDomain() {
        return Rental.reconstitute(
                new RentalId(rentalId),
                new RenterId(renterId),
                new ProviderId(providerId),
                new VehicleId(vehicleId),
                vehicleType,
                externalRentalId,
                startedAt,
                status,
                completedAt,
                vehicleLastSwappedAt);
    }
}