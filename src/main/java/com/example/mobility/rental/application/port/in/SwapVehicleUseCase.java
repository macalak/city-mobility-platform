package com.example.mobility.rental.application.port.in;

import com.example.mobility.rental.domain.Rental;

public interface SwapVehicleUseCase {
    Rental swapVehicle(SwapVehicleCommand command);
}