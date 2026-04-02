package com.challenge.swapi.service;

import static com.challenge.swapi.fixtures.TestFixtures.vehicle;
import static com.challenge.swapi.fixtures.TestFixtures.vehicleDetail;
import static com.challenge.swapi.fixtures.TestFixtures.vehiclesResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.challenge.swapi.client.SwapiClient;
import com.challenge.swapi.dto.VehicleDetailResponseDTO;
import com.challenge.swapi.dto.VehiclesResponseDTO;
import com.challenge.swapi.exception.ResourceNotFoundException;
import com.challenge.swapi.exception.UpstreamServiceException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

@ExtendWith(MockitoExtension.class)
class VehiclesServiceTest {

    @Mock
    private SwapiClient swapiClient;

    @InjectMocks
    private VehiclesService vehiclesService;

    @Test
    void getVehiclesFiltersByNameIgnoringCase() {
        VehiclesResponseDTO upstream = vehiclesResponse(
            vehicle("4", "Sand Crawler"),
            vehicle("6", "T-16 skyhopper")
        );
        when(swapiClient.getVehicles(eq(1), eq(10))).thenReturn(upstream);

        VehiclesResponseDTO result = vehiclesService.getVehicles("sand", 1, 10);

        assertThat(result.getResults()).hasSize(1);
        assertThat(result.getResults().getFirst().getName()).isEqualTo("Sand Crawler");
    }

    @Test
    void getVehiclesReturnsOriginalResponseWhenListIsEmpty() {
        VehiclesResponseDTO upstream = new VehiclesResponseDTO();
        upstream.setResults(java.util.List.of());
        when(swapiClient.getVehicles(eq(1), eq(10))).thenReturn(upstream);

        VehiclesResponseDTO result = vehiclesService.getVehicles("", 1, 10);

        assertThat(result.getResults()).isEmpty();
    }

    @Test
    void getVehicleByIdReturnsDetailFromClient() {
        VehicleDetailResponseDTO detail = vehicleDetail("ok");
        when(swapiClient.getVehicleById("1")).thenReturn(detail);

        VehicleDetailResponseDTO result = vehiclesService.getVehicleById("1");

        assertThat(result.getMessage()).isEqualTo("ok");
    }

    @Test
    void getVehicleByIdMapsNotFoundToResourceNotFoundException() {
        HttpClientErrorException.NotFound notFound = (HttpClientErrorException.NotFound) HttpClientErrorException.create(
            HttpStatus.NOT_FOUND,
            "Not Found",
            HttpHeaders.EMPTY,
            new byte[0],
            null
        );
        when(swapiClient.getVehicleById("999")).thenThrow(notFound);

        assertThatThrownBy(() -> vehiclesService.getVehicleById("999"))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Vehicle not found with id: 999");
    }

    @Test
    void getVehicleByIdMapsClientErrorsToUpstreamServiceException() {
        when(swapiClient.getVehicleById("1")).thenThrow(new RestClientException("timeout"));

        assertThatThrownBy(() -> vehiclesService.getVehicleById("1"))
            .isInstanceOf(UpstreamServiceException.class)
            .hasMessageContaining("SWAPI request failed while fetching vehicle with id: 1");
    }
}
