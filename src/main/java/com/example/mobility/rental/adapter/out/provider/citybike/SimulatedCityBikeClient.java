package com.example.mobility.rental.adapter.out.provider.citybike;

import com.example.mobility.rental.adapter.out.provider.citybike.client.CityBikeClient;
import com.example.mobility.rental.adapter.out.provider.citybike.client.CityBikeStartRentalRequest;
import com.example.mobility.rental.adapter.out.provider.citybike.client.CityBikeStartRentalResponse;
import com.example.mobility.rental.adapter.out.provider.citybike.client.CityBikeSwapVehicleRequest;
import com.example.mobility.rental.adapter.out.provider.citybike.client.CityBikeSwapVehicleResponse;

public class SimulatedCityBikeClient implements CityBikeClient {
    @Override
    public CityBikeStartRentalResponse startRental(CityBikeStartRentalRequest request) {
        if ("fail-city-bike".equals(request.customerReference()) || "fail-city-bike".equals(request.bikeIdentifier())) {
            throw new IllegalStateException("City bike provider unavailable");
        }
        return new CityBikeStartRentalResponse("city-bike-rental-" + request.bikeIdentifier(), request.bikeIdentifier());
    }

    @Override
    public CityBikeSwapVehicleResponse swapVehicle(CityBikeSwapVehicleRequest request) {
        if ("fail-city-bike".equals(request.bikeIdentifier())) {
            throw new IllegalStateException("City bike provider unavailable");
        }
        return new CityBikeSwapVehicleResponse(request.bikeIdentifier());
    }
}
