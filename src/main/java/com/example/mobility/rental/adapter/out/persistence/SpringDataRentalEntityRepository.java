package com.example.mobility.rental.adapter.out.persistence;

import com.example.mobility.rental.domain.RentalStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataRentalEntityRepository extends JpaRepository<RentalEntity, String> {
    boolean existsByRenterIdAndStatus(String renterId, RentalStatus status);
}