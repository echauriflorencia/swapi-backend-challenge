package com.challenge.swapi.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.test.web.client.MockRestServiceServer;

class SwapiClientTest {

    private RestTemplate restTemplate;
    private MockRestServiceServer server;
    private SwapiClient swapiClient;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        server = MockRestServiceServer.bindTo(restTemplate).ignoreExpectOrder(true).build();
        swapiClient = new SwapiClient(restTemplate, "https://www.swapi.tech/api/");
    }

    @Test
    void getPeopleBuildsExpectedUrlAndParsesPayload() {
        server.expect(requestTo("https://www.swapi.tech/api/people?page=2&limit=5"))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(
                """
                {
                  \"message\": \"ok\",
                  \"total_records\": 1,
                  \"total_pages\": 1,
                  \"results\": [
                    { \"uid\": \"1\", \"name\": \"Luke Skywalker\" }
                  ]
                }
                """,
                MediaType.APPLICATION_JSON
            ));

        var response = swapiClient.getPeople(2, 5);

        assertThat(response.getResults()).hasSize(1);
        assertThat(response.getResults().getFirst().getName()).isEqualTo("Luke Skywalker");
        server.verify();
    }

    @Test
    void getFilmsParsesResultFieldFromSwapi() {
        server.expect(requestTo("https://www.swapi.tech/api/films?page=1&limit=10"))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(
                """
                {
                  \"message\": \"ok\",
                  \"total_records\": 1,
                  \"total_pages\": 1,
                  \"result\": [
                    {
                      \"uid\": \"1\",
                      \"properties\": { \"title\": \"A New Hope\" }
                    }
                  ]
                }
                """,
                MediaType.APPLICATION_JSON
            ));

        var response = swapiClient.getFilms(1, 10);

        assertThat(response.getResult()).hasSize(1);
        assertThat(response.getResult().getFirst().getProperties().getTitle()).isEqualTo("A New Hope");
        server.verify();
    }

    @Test
    void getStarshipByIdPropagatesRestClientExceptionLikeTimeout() {
        server.expect(requestTo("https://www.swapi.tech/api/starships/9"))
            .andExpect(method(HttpMethod.GET))
            .andRespond(request -> {
                throw new ResourceAccessException("Read timed out");
            });

        assertThatThrownBy(() -> swapiClient.getStarshipById("9"))
            .isInstanceOf(ResourceAccessException.class)
            .hasMessageContaining("Read timed out");
    }
}
