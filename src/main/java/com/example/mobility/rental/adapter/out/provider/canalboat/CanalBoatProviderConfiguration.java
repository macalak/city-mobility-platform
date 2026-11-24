package com.example.mobility.rental.adapter.out.provider.canalboat;

import com.example.mobility.rental.adapter.out.provider.canalboat.client.CanalBoatClient;
import com.example.mobility.rental.application.port.out.RentalProviderPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CanalBoatProviderConfiguration {

    @Bean
    CanalBoatClient canalBoatClient() {
        return new SimulatedCanalBoatClient();
    }

    @Bean
    RentalProviderPort canalBoatRentalAdapter(CanalBoatClient client) {
        return new CanalBoatRentalAdapter(client);
    }
}
