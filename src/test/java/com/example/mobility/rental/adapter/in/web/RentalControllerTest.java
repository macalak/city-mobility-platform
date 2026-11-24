package com.example.mobility.rental.adapter.in.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.mobility.rental.application.port.in.StartRentalCommand;
import com.example.mobility.rental.application.port.in.StartRentalUseCase;
import com.example.mobility.rental.application.port.in.SwapVehicleUseCase;
import com.example.mobility.rental.domain.RentalId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(RentalController.class)
class RentalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StartRentalUseCase startRentalUseCase;

    @MockBean
    private SwapVehicleUseCase swapVehicleUseCase;

    @Test
    void startsRental() throws Exception {
        given(startRentalUseCase.startRental(any(StartRentalCommand.class)))
                .willReturn(new RentalId("r-123"));

        mockMvc.perform(post("/api/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"renterId\":\"anna\",\"providerId\":\"city-bike\",\"vehicleId\":\"bike-42\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rentalId").value("r-123"));
    }
}
