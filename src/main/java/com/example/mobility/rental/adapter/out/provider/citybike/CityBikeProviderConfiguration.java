package com.example.mobility.rental.adapter.out.provider.citybike;

import com.example.mobility.rental.adapter.out.provider.citybike.client.CityBikeClient;
import com.example.mobility.rental.application.port.out.RentalProviderPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CityBikeProviderConfiguration {

    @Bean
    CityBikeClient cityBikeClient() {
        return new SimulatedCityBikeClient();
    }

    @Bean
    RentalProviderPort cityBikeRentalAdapter(CityBikeClient client) {
        return new CityBikeRentalAdapter(client);
    }
}
