package com.challenge.swapi.service;

import static com.challenge.swapi.fixtures.TestFixtures.peopleResponse;
import static com.challenge.swapi.fixtures.TestFixtures.person;
import static com.challenge.swapi.fixtures.TestFixtures.personDetail;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.challenge.swapi.client.SwapiClient;
import com.challenge.swapi.dto.PeopleResponseDTO;
import com.challenge.swapi.dto.PersonDetailResponseDTO;
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
class PeopleServiceTest {

    @Mock
    private SwapiClient swapiClient;

    @InjectMocks
    private PeopleService peopleService;

    @Test
    void getPeopleReturnsOriginalResponseWhenNameFilterIsEmpty() {
        PeopleResponseDTO upstream = peopleResponse(person("1", "Luke Skywalker"), person("2", "Leia Organa"));
        when(swapiClient.getPeople(eq(1), eq(10))).thenReturn(upstream);

        PeopleResponseDTO result = peopleService.getPeople("", 1, 10);

        assertThat(result.getResults()).hasSize(2);
    }

    @Test
    void getPeopleFiltersByNameIgnoringCase() {
        PeopleResponseDTO upstream = peopleResponse(
            person("1", "Luke Skywalker"),
            person("2", "Leia Organa"),
            person("3", "Han Solo")
        );
        when(swapiClient.getPeople(eq(1), eq(10))).thenReturn(upstream);

        PeopleResponseDTO result = peopleService.getPeople("le", 1, 10);

        assertThat(result.getResults()).extracting("name").containsExactly("Leia Organa");
    }

    @Test
    void getPersonByIdReturnsDetailFromClient() {
        PersonDetailResponseDTO detail = personDetail("ok");
        when(swapiClient.getPersonById("1")).thenReturn(detail);

        PersonDetailResponseDTO result = peopleService.getPersonById("1");

        assertThat(result.getMessage()).isEqualTo("ok");
    }

    @Test
    void getPersonByIdMapsNotFoundToResourceNotFoundException() {
        HttpClientErrorException.NotFound notFound = (HttpClientErrorException.NotFound) HttpClientErrorException.create(
            HttpStatus.NOT_FOUND,
            "Not Found",
            HttpHeaders.EMPTY,
            new byte[0],
            null
        );
        when(swapiClient.getPersonById("999")).thenThrow(notFound);

        assertThatThrownBy(() -> peopleService.getPersonById("999"))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Person not found with id: 999");
    }

    @Test
    void getPersonByIdMapsClientErrorsToUpstreamServiceException() {
        when(swapiClient.getPersonById("1")).thenThrow(new RestClientException("timeout"));

        assertThatThrownBy(() -> peopleService.getPersonById("1"))
            .isInstanceOf(UpstreamServiceException.class)
            .hasMessageContaining("SWAPI request failed while fetching person with id: 1");
    }
}
