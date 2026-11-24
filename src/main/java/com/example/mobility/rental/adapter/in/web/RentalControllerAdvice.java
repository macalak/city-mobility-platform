package com.example.mobility.rental.adapter.in.web;

import com.example.mobility.rental.application.exception.ProviderIntegrationException;
import com.example.mobility.rental.application.exception.RenterAlreadyHasActiveRental;
import com.example.mobility.rental.application.exception.RentalNotFoundException;
import com.example.mobility.rental.application.exception.UnknownRentalProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RentalControllerAdvice {

    @ExceptionHandler(UnknownRentalProvider.class)
    public ResponseEntity<ApiErrorResponse> handleUnknownProvider(UnknownRentalProvider ex) {
        return error(HttpStatus.NOT_FOUND, "UNKNOWN_RENTAL_PROVIDER", ex.getMessage());
    }

    @ExceptionHandler(RenterAlreadyHasActiveRental.class)
    public ResponseEntity<ApiErrorResponse> handleActiveRental(RenterAlreadyHasActiveRental ex) {
        return error(HttpStatus.CONFLICT, "ACTIVE_RENTAL_EXISTS", ex.getMessage());
    }

    @ExceptionHandler(RentalNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleRentalNotFound(RentalNotFoundException ex) {
        return error(HttpStatus.NOT_FOUND, "RENTAL_NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(ProviderIntegrationException.class)
    public ResponseEntity<ApiErrorResponse> handleProviderIntegration(ProviderIntegrationException ex) {
        return error(HttpStatus.BAD_GATEWAY, "PROVIDER_UNAVAILABLE", ex.getMessage());
    }

    @ExceptionHandler({
        IllegalArgumentException.class,
        MethodArgumentNotValidException.class,
        BindException.class,
        HttpMessageNotReadableException.class
    })
    public ResponseEntity<ApiErrorResponse> handleInvalidRequest(Exception ex) {
        return error(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", ex.getMessage());
    }

    private ResponseEntity<ApiErrorResponse> error(HttpStatus status, String code, String message) {
        return ResponseEntity.status(status).body(new ApiErrorResponse(code, message));
    }
}
