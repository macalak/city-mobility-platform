package com.example.mobility.layeredrental.datasource.integration;

import org.springframework.stereotype.Component;

@Component
public class LayeredCanalBoatClient {
    public Response startRental(Request request) {
        if ("fail-canal-boat".equals(request.passengerReference()) || "fail-canal-boat".equals(request.vesselCode())) {
            throw new IllegalStateException("Canal boat provider unavailable");
        }
        return new Response("canal-boat-rental-" + request.vesselCode(), request.vesselCode());
    }

    public record Request(String passengerReference, String vesselCode) {}

    public record Response(String externalRentalId, String vesselCode) {}
}