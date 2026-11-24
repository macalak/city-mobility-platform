package com.example.mobility.layeredrental.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/**
 * An anemic model that also serves as a JPA entity; rental rules live in the service.
 */
@Entity
@Table(name = "layered_rentals")
public class LayeredRental {
        @Id
        @Column(name = "rental_id", nullable = false, updatable = false)
        private String rentalId;

        @Column(name = "renter_id", nullable = false)
        private String renterId;

        @Column(name = "provider_id", nullable = false)
        private String providerId;

        @Column(name = "vehicle_id", nullable = false)
        private String vehicleId;

        @Column(name = "vehicle_type", nullable = false)
        private String vehicleType;

        @Column(name = "external_rental_id", nullable = false)
        private String externalRentalId;

        @Column(name = "started_at", nullable = false)
        private Instant startedAt;

        @Column(nullable = false)
        private String status;

        protected LayeredRental() {}

        public LayeredRental(String rentalId, String renterId, String providerId, String vehicleId,
                        String vehicleType, String externalRentalId, Instant startedAt, String status) {
                this.rentalId = rentalId;
                this.renterId = renterId;
                this.providerId = providerId;
                this.vehicleId = vehicleId;
                this.vehicleType = vehicleType;
                this.externalRentalId = externalRentalId;
                this.startedAt = startedAt;
                this.status = status;
        }

        public String rentalId() { return rentalId; }
        public String renterId() { return renterId; }
        public String providerId() { return providerId; }
        public String vehicleId() { return vehicleId; }
        public String vehicleType() { return vehicleType; }
        public String externalRentalId() { return externalRentalId; }
        public Instant startedAt() { return startedAt; }
        public String status() { return status; }
}