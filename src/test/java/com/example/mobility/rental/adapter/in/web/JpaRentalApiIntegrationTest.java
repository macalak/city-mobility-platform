package com.example.mobility.rental.adapter.in.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("jpa")
class JpaRentalApiIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void jpaProfilePersistsRentalsAndEnforcesActiveRentalRule() throws Exception {
        String renterId = "jpa-renter-" + UUID.randomUUID();
        String request = "{\"renterId\":\"" + renterId
                + "\",\"providerId\":\"city-bike\",\"vehicleId\":\"bike-42\"}";

        mockMvc.perform(post("/api/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("ACTIVE_RENTAL_EXISTS"));
    }

    @Test
    void jpaProfilePersistsVehicleSwap() throws Exception {
        String request = "{\"renterId\":\"jpa-swap-" + UUID.randomUUID()
                + "\",\"providerId\":\"city-bike\",\"vehicleId\":\"bike-42\"}";
        MvcResult created = mockMvc.perform(post("/api/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andReturn();
        String location = created.getResponse().getHeader("Location");
        String rentalId = location.substring(location.lastIndexOf('/') + 1);

        mockMvc.perform(put("/api/rentals/" + rentalId + "/vehicle")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"replacementVehicleId\":\"bike-43\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vehicleId").value("bike-43"))
                .andExpect(jsonPath("$.swappedAt").isNotEmpty());
    }
}