package com.example.mobility.layeredrental.datasource.persistence;

import com.example.mobility.layeredrental.domain.LayeredRental;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LayeredRentalRepository extends JpaRepository<LayeredRental, String> {
    boolean existsByRenterIdAndStatus(String renterId, String status);
}