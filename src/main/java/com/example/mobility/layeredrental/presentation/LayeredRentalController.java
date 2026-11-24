package com.example.mobility.layeredrental.presentation;

import com.example.mobility.layeredrental.domain.LayeredRental;
import com.example.mobility.layeredrental.domain.LayeredRentalException;
import com.example.mobility.layeredrental.domain.LayeredRentalService;
import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/layered/rentals")
public class LayeredRentalController {
    private final LayeredRentalService service;

    public LayeredRentalController(LayeredRentalService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<RentalCreatedResponse> startRental(@RequestBody StartRentalRequest request) {
        LayeredRental rental = service.startRental(request.renterId(), request.providerId(), request.vehicleId());
        return ResponseEntity.created(URI.create("/api/layered/rentals/" + rental.rentalId()))
                .body(new RentalCreatedResponse(rental.rentalId(), rental.status()));
    }

    @ExceptionHandler(LayeredRentalException.class)
    public ResponseEntity<ErrorResponse> handleRentalError(LayeredRentalException exception) {
        return ResponseEntity.status(exception.status()).body(new ErrorResponse(exception.code(), exception.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleInvalidBody(HttpMessageNotReadableException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("INVALID_REQUEST", exception.getMessage()));
    }

    public record StartRentalRequest(String renterId, String providerId, String vehicleId) {}

    public record RentalCreatedResponse(String rentalId, String status) {}

    public record ErrorResponse(String code, String message) {}
}