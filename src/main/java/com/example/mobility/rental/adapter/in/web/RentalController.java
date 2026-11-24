package com.example.mobility.rental.adapter.in.web;

import com.example.mobility.rental.application.port.in.StartRentalUseCase;
import com.example.mobility.rental.application.port.in.SwapVehicleCommand;
import com.example.mobility.rental.application.port.in.SwapVehicleUseCase;
import com.example.mobility.rental.domain.Rental;
import com.example.mobility.rental.domain.RentalId;
import com.example.mobility.rental.domain.VehicleId;
import java.net.URI;
import java.time.Instant;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class RentalController {
    private final StartRentalUseCase startRentalUseCase;
    private final SwapVehicleUseCase swapVehicleUseCase;

    public RentalController(StartRentalUseCase startRentalUseCase, SwapVehicleUseCase swapVehicleUseCase) {
        this.startRentalUseCase = startRentalUseCase;
        this.swapVehicleUseCase = swapVehicleUseCase;
    }

    @PostMapping("/rentals")
    public ResponseEntity<RentalCreatedResponse> startRental(@RequestBody StartRentalRequest request) {
        RentalId rentalId = startRentalUseCase.startRental(request.toCommand());
        return ResponseEntity.created(URI.create("/api/rentals/" + rentalId.value()))
                .body(new RentalCreatedResponse(rentalId.value(), "ACTIVE"));
    }

    @PutMapping("/rentals/{rentalId}/vehicle")
    public ResponseEntity<VehicleSwapResponse> swapVehicle(
            @PathVariable String rentalId,
            @RequestBody SwapVehicleRequest request) {
        Rental rental = swapVehicleUseCase.swapVehicle(request.toCommand(new RentalId(rentalId)));
        return ResponseEntity.ok(new VehicleSwapResponse(
                rental.rentalId().value(), rental.vehicleId().value(), rental.vehicleLastSwappedAt()));
    }

    public record SwapVehicleRequest(String replacementVehicleId) {
        public SwapVehicleCommand toCommand(RentalId rentalId) {
            return new SwapVehicleCommand(rentalId, new VehicleId(replacementVehicleId));
        }
    }

    public record VehicleSwapResponse(String rentalId, String vehicleId, Instant swappedAt) {}
}