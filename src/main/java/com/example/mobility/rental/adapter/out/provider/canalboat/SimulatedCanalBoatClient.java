package com.example.mobility.rental.adapter.out.provider.canalboat;

import com.example.mobility.rental.adapter.out.provider.canalboat.client.CanalBoatClient;
import com.example.mobility.rental.adapter.out.provider.canalboat.client.CanalBoatStartRentalRequest;
import com.example.mobility.rental.adapter.out.provider.canalboat.client.CanalBoatStartRentalResponse;
import com.example.mobility.rental.adapter.out.provider.canalboat.client.CanalBoatSwapVehicleRequest;
import com.example.mobility.rental.adapter.out.provider.canalboat.client.CanalBoatSwapVehicleResponse;

public class SimulatedCanalBoatClient implements CanalBoatClient {
    @Override
    public CanalBoatStartRentalResponse startRental(CanalBoatStartRentalRequest request) {
        if ("fail-canal-boat".equals(request.passengerReference()) || "fail-canal-boat".equals(request.vesselCode())) {
            throw new IllegalStateException("Canal boat provider unavailable");
        }
        return new CanalBoatStartRentalResponse("canal-boat-rental-" + request.vesselCode(), request.vesselCode());
    }

    @Override
    public CanalBoatSwapVehicleResponse swapVehicle(CanalBoatSwapVehicleRequest request) {
        if ("fail-canal-boat".equals(request.vesselCode())) {
            throw new IllegalStateException("Canal boat provider unavailable");
        }
        return new CanalBoatSwapVehicleResponse(request.vesselCode());
    }
}
