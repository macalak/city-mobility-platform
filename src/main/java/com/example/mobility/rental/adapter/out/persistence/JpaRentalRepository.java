package com.example.mobility.rental.adapter.out.persistence;

import com.example.mobility.rental.application.port.out.RentalRepository;
import com.example.mobility.rental.domain.Rental;
import com.example.mobility.rental.domain.RentalId;
import com.example.mobility.rental.domain.RentalStatus;
import com.example.mobility.rental.domain.RenterId;
import java.util.Optional;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("jpa")
public class JpaRentalRepository implements RentalRepository {
    private final SpringDataRentalEntityRepository repository;

    public JpaRentalRepository(SpringDataRentalEntityRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean hasActiveRentalFor(RenterId renterId) {
        return repository.existsByRenterIdAndStatus(renterId.value(), RentalStatus.ACTIVE);
    }

    @Override
    public void save(Rental rental) {
        repository.save(RentalEntity.fromDomain(rental));
    }

    @Override
    public Optional<Rental> findById(RentalId rentalId) {
        return repository.findById(rentalId.value()).map(RentalEntity::toDomain);
    }
}