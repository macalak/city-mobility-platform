package com.example.mobility.rental;

import com.example.mobility.rental.adapter.out.persistence.InMemoryRentalRepository;
import com.example.mobility.rental.application.port.out.RentalProviderPort;
import com.example.mobility.rental.application.port.out.RentalRepository;
import com.example.mobility.rental.application.port.out.TimeProvider;
import com.example.mobility.rental.application.service.RentalApplicationService;
import com.example.mobility.rental.application.service.RentalProviderRegistry;
import java.time.Instant;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class RentalConfiguration {

    @Bean
    TimeProvider systemTimeProvider() {
        return Instant::now;
    }

    @Bean
    @Profile("!jpa")
    RentalRepository rentalRepository() {
        return new InMemoryRentalRepository();
    }

    @Bean
    RentalProviderRegistry rentalProviderRegistry(List<RentalProviderPort> providers) {
        return new RentalProviderRegistry(providers);
    }

    @Bean
    RentalApplicationService rentalApplicationService(
            RentalProviderRegistry rentalProviderRegistry,
            RentalRepository rentalRepository,
            TimeProvider timeProvider) {
        return new RentalApplicationService(rentalProviderRegistry, rentalRepository, timeProvider);
    }
}
