package com.example.mobility.rental.adapter.out.provider.citybike.client;

public interface CityBikeClient {
    CityBikeStartRentalResponse startRental(CityBikeStartRentalRequest request);

    CityBikeSwapVehicleResponse swapVehicle(CityBikeSwapVehicleRequest request);
}
