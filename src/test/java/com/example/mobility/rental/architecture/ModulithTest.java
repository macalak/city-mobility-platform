package com.example.mobility.rental.architecture;

import com.example.mobility.MobilityApplication;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ModulithTest {

    @Test
    void applicationModulesShouldBeValid() {
        ApplicationModules.of(MobilityApplication.class).verify();
    }
}
