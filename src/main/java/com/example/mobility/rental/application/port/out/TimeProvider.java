package com.example.mobility.rental.application.port.out;

import java.time.Instant;

@FunctionalInterface
public interface TimeProvider {
    Instant now();
}
