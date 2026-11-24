package com.example.mobility.rental.adapter.out.provider.canalboat.client;

public interface CanalBoatClient {
    CanalBoatStartRentalResponse startRental(CanalBoatStartRentalRequest request);

    CanalBoatSwapVehicleResponse swapVehicle(CanalBoatSwapVehicleRequest request);
}
