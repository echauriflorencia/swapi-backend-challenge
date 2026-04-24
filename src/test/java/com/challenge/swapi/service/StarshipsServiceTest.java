package com.challenge.swapi.service;

import static com.challenge.swapi.fixtures.TestFixtures.starship;
import static com.challenge.swapi.fixtures.TestFixtures.starshipDetail;
import static com.challenge.swapi.fixtures.TestFixtures.starshipsResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.challenge.swapi.client.SwapiClient;
import com.challenge.swapi.dto.StarshipDetailResponseDTO;
import com.challenge.swapi.dto.StarshipsResponseDTO;
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
class StarshipsServiceTest {

    @Mock
    private SwapiClient swapiClient;

    @InjectMocks
    private StarshipsService starshipsService;

    @Test
    void getStarshipsFiltersByNameIgnoringCase() {
        StarshipsResponseDTO upstream = starshipsResponse(
            starship("1", "Death Star"),
            starship("2", "X-wing")
        );
        when(swapiClient.getStarships(eq(1), eq(50))).thenReturn(upstream);

        StarshipsResponseDTO result = starshipsService.getStarships("", "star", 1, 10);

        assertThat(result.getResults()).hasSize(1);
        assertThat(result.getResults().getFirst().getName()).isEqualTo("Death Star");
    }

    @Test
    void getStarshipsReturnsOriginalResponseWhenListIsEmpty() {
        StarshipsResponseDTO upstream = new StarshipsResponseDTO();
        upstream.setResults(java.util.List.of());
        when(swapiClient.getStarships(eq(1), eq(10))).thenReturn(upstream);

        StarshipsResponseDTO result = starshipsService.getStarships("", "", 1, 10);

        assertThat(result.getResults()).isEmpty();
    }

    @Test
    void getStarshipsFiltersByIdAndName() {
        StarshipsResponseDTO upstream = starshipsResponse(
            starship("1", "Death Star"),
            starship("2", "X-wing")
        );
        when(swapiClient.getStarships(eq(1), eq(50))).thenReturn(upstream);

        StarshipsResponseDTO result = starshipsService.getStarships("1", "death", 1, 10);

        assertThat(result.getResults()).hasSize(1);
        assertThat(result.getResults().getFirst().getUid()).isEqualTo("1");
    }

    @Test
    void getStarshipsReturnsEmptyWhenFilterHasNoMatch() {
        StarshipsResponseDTO upstream = starshipsResponse(
            starship("1", "Death Star"),
            starship("2", "X-wing")
        );
        when(swapiClient.getStarships(eq(1), eq(50))).thenReturn(upstream);

        StarshipsResponseDTO result = starshipsService.getStarships("", "falcon", 1, 10);

        assertThat(result.getResults()).isEmpty();
        assertThat(result.getTotal_records()).isZero();
    }

    @Test
    void getStarshipByIdReturnsDetailFromClient() {
        StarshipDetailResponseDTO detail = starshipDetail("ok");
        when(swapiClient.getStarshipById("1")).thenReturn(detail);

        StarshipDetailResponseDTO result = starshipsService.getStarshipById("1");

        assertThat(result.getMessage()).isEqualTo("ok");
    }

    @Test
    void getStarshipByIdMapsNotFoundToResourceNotFoundException() {
        HttpClientErrorException.NotFound notFound = (HttpClientErrorException.NotFound) HttpClientErrorException.create(
            HttpStatus.NOT_FOUND,
            "Not Found",
            HttpHeaders.EMPTY,
            new byte[0],
            null
        );
        when(swapiClient.getStarshipById("999")).thenThrow(notFound);

        assertThatThrownBy(() -> starshipsService.getStarshipById("999"))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Starship not found with id: 999");
    }

    @Test
    void getStarshipByIdMapsClientErrorsToUpstreamServiceException() {
        when(swapiClient.getStarshipById("1")).thenThrow(new RestClientException("timeout"));

        assertThatThrownBy(() -> starshipsService.getStarshipById("1"))
            .isInstanceOf(UpstreamServiceException.class)
            .hasMessageContaining("SWAPI request failed while fetching starship with id: 1");
    }
}
