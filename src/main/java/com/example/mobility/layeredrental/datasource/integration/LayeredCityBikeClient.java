package com.example.mobility.layeredrental.datasource.integration;

import org.springframework.stereotype.Component;

@Component
public class LayeredCityBikeClient {
    public Response startRental(Request request) {
        if ("fail-city-bike".equals(request.customerReference()) || "fail-city-bike".equals(request.bikeIdentifier())) {
            throw new IllegalStateException("City bike provider unavailable");
        }
        return new Response("city-bike-rental-" + request.bikeIdentifier(), request.bikeIdentifier());
    }

    public record Request(String customerReference, String bikeIdentifier) {}

    public record Response(String externalRentalId, String bikeId) {}
}