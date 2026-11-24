package com.example.mobility.rental.adapter.out.persistence;

import com.example.mobility.rental.application.port.out.RentalRepository;
import com.example.mobility.rental.domain.Rental;
import com.example.mobility.rental.domain.RentalId;
import com.example.mobility.rental.domain.RenterId;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryRentalRepository implements RentalRepository {
    private final Map<RentalId, Rental> rentals = new ConcurrentHashMap<>();

    @Override
    public boolean hasActiveRentalFor(RenterId renterId) {
        return rentals.values().stream()
                .anyMatch(rental -> rental.renterId().equals(renterId) && rental.status().name().equals("ACTIVE"));
    }

    @Override
    public void save(Rental rental) {
        if (rental == null) {
            throw new IllegalArgumentException("Rental is required");
        }
        rentals.put(rental.rentalId(), rental);
    }

    @Override
    public Optional<Rental> findById(RentalId rentalId) {
        return Optional.ofNullable(rentals.get(rentalId));
    }
}
