package com.example.mobility.rental.adapter.in.web;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class RentalApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void openApiDocumentIncludesRentalEndpoint() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("\"/api/rentals\"")));
    }

    @Test
    void swaggerUiIsAvailable() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML));
    }

    @Test
    void cityBikeRentalStartsSuccessfully() throws Exception {
        String renterId = "anna-" + UUID.randomUUID();

        mockMvc.perform(post("/api/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"renterId\":\"" + renterId + "\",\"providerId\":\"city-bike\",\"vehicleId\":\"bike-42\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.startsWith("/api/rentals/")))
                .andExpect(jsonPath("$.rentalId").isNotEmpty())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void canalBoatRentalStartsSuccessfully() throws Exception {
        String renterId = "bob-" + UUID.randomUUID();

        mockMvc.perform(post("/api/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"renterId\":\"" + renterId + "\",\"providerId\":\"canal-boat\",\"vehicleId\":\"boat-7\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void unknownProviderReturnsMachineReadableError() throws Exception {
        mockMvc.perform(post("/api/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"renterId\":\"anna\",\"providerId\":\"green-scooter\",\"vehicleId\":\"scooter-17\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("UNKNOWN_RENTAL_PROVIDER"));
    }

    @Test
    void invalidRequestReturnsValidationError() throws Exception {
        mockMvc.perform(post("/api/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"renterId\":\"\",\"providerId\":\"city-bike\",\"vehicleId\":\"bike-42\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
    }

    @Test
    void activeRentalBlocksAnotherRental() throws Exception {
        String renterId = "charlie-" + UUID.randomUUID();
        mockMvc.perform(post("/api/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"renterId\":\"" + renterId + "\",\"providerId\":\"city-bike\",\"vehicleId\":\"bike-42\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"renterId\":\"" + renterId + "\",\"providerId\":\"canal-boat\",\"vehicleId\":\"boat-7\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("ACTIVE_RENTAL_EXISTS"));
    }

            @Test
            void activeRentalCanSwapVehicleAfterProviderConfirmation() throws Exception {
            String renterId = "swap-" + UUID.randomUUID();
            MvcResult created = mockMvc.perform(post("/api/rentals")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"renterId\":\"" + renterId
                        + "\",\"providerId\":\"city-bike\",\"vehicleId\":\"bike-42\"}"))
                .andExpect(status().isCreated())
                .andReturn();
            String location = created.getResponse().getHeader("Location");
            String rentalId = location.substring(location.lastIndexOf('/') + 1);

            mockMvc.perform(put("/api/rentals/" + rentalId + "/vehicle")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"replacementVehicleId\":\"bike-43\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rentalId").value(rentalId))
                .andExpect(jsonPath("$.vehicleId").value("bike-43"))
                .andExpect(jsonPath("$.swappedAt").isNotEmpty());
            }

            @Test
            void providerSwapFailureDoesNotUpdateRentalAndUnknownRentalReturnsNotFound() throws Exception {
            String renterId = "swap-failure-" + UUID.randomUUID();
            MvcResult created = mockMvc.perform(post("/api/rentals")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"renterId\":\"" + renterId
                        + "\",\"providerId\":\"city-bike\",\"vehicleId\":\"bike-42\"}"))
                .andExpect(status().isCreated())
                .andReturn();
            String location = created.getResponse().getHeader("Location");
            String rentalId = location.substring(location.lastIndexOf('/') + 1);

            mockMvc.perform(put("/api/rentals/" + rentalId + "/vehicle")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"replacementVehicleId\":\"fail-city-bike\"}"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.code").value("PROVIDER_UNAVAILABLE"));

            mockMvc.perform(put("/api/rentals/" + rentalId + "/vehicle")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"replacementVehicleId\":\"bike-43\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vehicleId").value("bike-43"));

            mockMvc.perform(put("/api/rentals/missing-rental/vehicle")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"replacementVehicleId\":\"bike-43\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RENTAL_NOT_FOUND"));
            }
}
