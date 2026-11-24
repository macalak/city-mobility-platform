package com.example.mobility.rental.application.port.out;

import com.example.mobility.rental.domain.Rental;
import com.example.mobility.rental.domain.RentalId;
import com.example.mobility.rental.domain.RenterId;
import java.util.Optional;

public interface RentalRepository {
    boolean hasActiveRentalFor(RenterId renterId);
    void save(Rental rental);
    Optional<Rental> findById(RentalId rentalId);
}
