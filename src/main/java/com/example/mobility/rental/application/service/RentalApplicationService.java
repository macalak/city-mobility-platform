package com.example.mobility.rental.application.service;

import com.example.mobility.rental.application.exception.RenterAlreadyHasActiveRental;
import com.example.mobility.rental.application.exception.RentalNotFoundException;
import com.example.mobility.rental.application.port.in.StartRentalCommand;
import com.example.mobility.rental.application.port.in.StartRentalUseCase;
import com.example.mobility.rental.application.port.in.SwapVehicleCommand;
import com.example.mobility.rental.application.port.in.SwapVehicleUseCase;
import com.example.mobility.rental.application.port.out.ProviderConfirmation;
import com.example.mobility.rental.application.port.out.RentalProviderPort;
import com.example.mobility.rental.application.port.out.RentalRepository;
import com.example.mobility.rental.application.port.out.TimeProvider;
import com.example.mobility.rental.domain.Rental;
import com.example.mobility.rental.domain.RentalId;
import com.example.mobility.rental.domain.VehicleId;
import java.time.Instant;
import java.util.UUID;

public class RentalApplicationService implements StartRentalUseCase, SwapVehicleUseCase {
    private final RentalProviderRegistry providerRegistry;
    private final RentalRepository rentalRepository;
    private final TimeProvider timeProvider;

    public RentalApplicationService(
            RentalProviderRegistry providerRegistry,
            RentalRepository rentalRepository,
            TimeProvider timeProvider) {
        this.providerRegistry = providerRegistry;
        this.rentalRepository = rentalRepository;
        this.timeProvider = timeProvider;
    }

    @Override
    public RentalId startRental(StartRentalCommand command) {
        if (rentalRepository.hasActiveRentalFor(command.renterId())) {
            throw new RenterAlreadyHasActiveRental(
                    "Renter already has an active rental: " + command.renterId().value());
        }

        RentalProviderPort provider = providerRegistry.get(command.providerId());
        ProviderConfirmation confirmation = provider.startRental(command.renterId(), command.vehicleId());
        Instant now = timeProvider.now();
        Rental rental = Rental.start(
                new RentalId(UUID.randomUUID().toString()),
                command.renterId(),
                command.providerId(),
                confirmation.vehicleId(),
                confirmation.vehicleType(),
                confirmation.externalRentalId(),
                now);

        rentalRepository.save(rental);
        return rental.rentalId();
    }

    @Override
    public Rental swapVehicle(SwapVehicleCommand command) {
        Rental rental = rentalRepository.findById(command.rentalId())
                .orElseThrow(() -> new RentalNotFoundException(
                        "Rental not found: " + command.rentalId().value()));
        rental.validateVehicleSwap(command.replacementVehicleId(), timeProvider.now());

        RentalProviderPort provider = providerRegistry.get(rental.providerId());
        VehicleId confirmedVehicleId = provider.swapVehicle(
                rental.externalRentalId(), command.replacementVehicleId());
        Rental swappedRental = rental.swapVehicle(confirmedVehicleId, timeProvider.now());

        rentalRepository.save(swappedRental);
        return swappedRental;
    }
}