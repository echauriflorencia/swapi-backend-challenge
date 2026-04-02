package com.challenge.swapi.service;

import static com.challenge.swapi.fixtures.TestFixtures.film;
import static com.challenge.swapi.fixtures.TestFixtures.filmDetail;
import static com.challenge.swapi.fixtures.TestFixtures.filmsResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.challenge.swapi.client.SwapiClient;
import com.challenge.swapi.dto.FilmDetailResponseDTO;
import com.challenge.swapi.dto.FilmsResponseDTO;
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
class FilmsServiceTest {

    @Mock
    private SwapiClient swapiClient;

    @InjectMocks
    private FilmsService filmsService;

    @Test
    void getFilmsFiltersByTitleAndPaginates() {
        FilmsResponseDTO upstream = filmsResponse(
            film("1", "A New Hope"),
            film("2", "The Empire Strikes Back"),
            film("3", "Return of the Jedi")
        );
        when(swapiClient.getFilms(eq(1), eq(50))).thenReturn(upstream);

        FilmsResponseDTO result = filmsService.getFilms("", "the", 1, 1);

        assertThat(result.getResult()).hasSize(1);
        assertThat(result.getResult().getFirst().getProperties().getTitle()).isEqualTo("The Empire Strikes Back");
    }

    @Test
    void getFilmsReturnsEmptyResultWhenPageIsOutOfRangeAfterFiltering() {
        FilmsResponseDTO upstream = filmsResponse(
            film("1", "A New Hope"),
            film("2", "The Empire Strikes Back")
        );
        when(swapiClient.getFilms(eq(1), eq(50))).thenReturn(upstream);

        FilmsResponseDTO result = filmsService.getFilms("", "the", 3, 1);

        assertThat(result.getResult()).isEmpty();
    }

    @Test
    void getFilmsFiltersByIdAndTitle() {
        FilmsResponseDTO upstream = filmsResponse(
            film("1", "A New Hope"),
            film("2", "The Empire Strikes Back")
        );
        when(swapiClient.getFilms(eq(1), eq(50))).thenReturn(upstream);

        FilmsResponseDTO result = filmsService.getFilms("2", "empire", 1, 10);

        assertThat(result.getResult()).hasSize(1);
        assertThat(result.getResult().getFirst().getUid()).isEqualTo("2");
    }

    @Test
    void getFilmByIdReturnsDetailFromClient() {
        FilmDetailResponseDTO detail = filmDetail("ok");
        when(swapiClient.getFilmById("1")).thenReturn(detail);

        FilmDetailResponseDTO result = filmsService.getFilmById("1");

        assertThat(result.getMessage()).isEqualTo("ok");
    }

    @Test
    void getFilmByIdMapsNotFoundToResourceNotFoundException() {
        HttpClientErrorException.NotFound notFound = (HttpClientErrorException.NotFound) HttpClientErrorException.create(
            HttpStatus.NOT_FOUND,
            "Not Found",
            HttpHeaders.EMPTY,
            new byte[0],
            null
        );
        when(swapiClient.getFilmById("999")).thenThrow(notFound);

        assertThatThrownBy(() -> filmsService.getFilmById("999"))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Film not found with id: 999");
    }

    @Test
    void getFilmByIdMapsClientErrorsToUpstreamServiceException() {
        when(swapiClient.getFilmById("1")).thenThrow(new RestClientException("timeout"));

        assertThatThrownBy(() -> filmsService.getFilmById("1"))
            .isInstanceOf(UpstreamServiceException.class)
            .hasMessageContaining("SWAPI request failed while fetching film with id: 1");
    }
}
