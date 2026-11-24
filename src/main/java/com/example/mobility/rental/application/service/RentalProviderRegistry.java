package com.example.mobility.rental.application.service;

import com.example.mobility.rental.application.exception.UnknownRentalProvider;
import com.example.mobility.rental.application.port.out.RentalProviderPort;
import com.example.mobility.rental.domain.ProviderId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RentalProviderRegistry {
    private final Map<ProviderId, RentalProviderPort> providers;

    public RentalProviderRegistry(List<RentalProviderPort> providers) {
        this.providers = new HashMap<>();
        for (RentalProviderPort provider : providers) {
            ProviderId providerId = provider.providerId();
            if (this.providers.containsKey(providerId)) {
                throw new IllegalArgumentException("Duplicate provider ID: " + providerId.value());
            }
            this.providers.put(providerId, provider);
        }
    }

    public RentalProviderPort get(ProviderId providerId) {
        RentalProviderPort provider = providers.get(providerId);
        if (provider == null) {
            throw new UnknownRentalProvider("Unknown rental provider: " + providerId.value());
        }
        return provider;
    }
}
