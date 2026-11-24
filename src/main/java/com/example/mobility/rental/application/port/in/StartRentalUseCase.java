package com.example.mobility.rental.application.port.in;

import com.example.mobility.rental.domain.RentalId;

public interface StartRentalUseCase {
    RentalId startRental(StartRentalCommand command);
}