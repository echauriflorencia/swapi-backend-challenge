package com.challenge.swapi.fixtures;

import com.challenge.swapi.dto.FilmDTO;
import com.challenge.swapi.dto.FilmDetailResponseDTO;
import com.challenge.swapi.dto.FilmPropertiesDTO;
import com.challenge.swapi.dto.FilmsResponseDTO;
import com.challenge.swapi.dto.PeopleResponseDTO;
import com.challenge.swapi.dto.PersonDTO;
import com.challenge.swapi.dto.PersonDetailResponseDTO;
import com.challenge.swapi.dto.StarshipDTO;
import com.challenge.swapi.dto.StarshipDetailResponseDTO;
import com.challenge.swapi.dto.StarshipsResponseDTO;
import com.challenge.swapi.dto.VehicleDTO;
import com.challenge.swapi.dto.VehicleDetailResponseDTO;
import com.challenge.swapi.dto.VehiclesResponseDTO;

import java.util.List;

public final class TestFixtures {

    private TestFixtures() {
    }

    public static PeopleResponseDTO peopleResponse(PersonDTO... people) {
        PeopleResponseDTO response = new PeopleResponseDTO();
        response.setMessage("ok");
        response.setResults(List.of(people));
        return response;
    }

    public static PersonDTO person(String uid, String name) {
        PersonDTO dto = new PersonDTO();
        dto.setUid(uid);
        dto.setName(name);
        dto.setUrl("https://www.swapi.tech/api/people/" + uid);
        return dto;
    }

    public static PersonDetailResponseDTO personDetail(String message) {
        PersonDetailResponseDTO dto = new PersonDetailResponseDTO();
        dto.setMessage(message);
        return dto;
    }

    public static FilmsResponseDTO filmsResponse(FilmDTO... films) {
        FilmsResponseDTO response = new FilmsResponseDTO();
        response.setMessage("ok");
        response.setResult(List.of(films));
        return response;
    }

    public static FilmDTO film(String uid, String title) {
        FilmPropertiesDTO properties = new FilmPropertiesDTO();
        properties.setTitle(title);

        FilmDTO dto = new FilmDTO();
        dto.setUid(uid);
        dto.setDescription("description " + uid);
        dto.setProperties(properties);
        return dto;
    }

    public static FilmDetailResponseDTO filmDetail(String message) {
        FilmDetailResponseDTO dto = new FilmDetailResponseDTO();
        dto.setMessage(message);
        return dto;
    }

    public static StarshipsResponseDTO starshipsResponse(StarshipDTO... starships) {
        StarshipsResponseDTO response = new StarshipsResponseDTO();
        response.setMessage("ok");
        response.setResults(List.of(starships));
        return response;
    }

    public static StarshipDTO starship(String uid, String name) {
        StarshipDTO dto = new StarshipDTO();
        dto.setUid(uid);
        dto.setName(name);
        dto.setUrl("https://www.swapi.tech/api/starships/" + uid);
        return dto;
    }

    public static StarshipDetailResponseDTO starshipDetail(String message) {
        StarshipDetailResponseDTO dto = new StarshipDetailResponseDTO();
        dto.setMessage(message);
        return dto;
    }

    public static VehiclesResponseDTO vehiclesResponse(VehicleDTO... vehicles) {
        VehiclesResponseDTO response = new VehiclesResponseDTO();
        response.setMessage("ok");
        response.setResults(List.of(vehicles));
        return response;
    }

    public static VehicleDTO vehicle(String uid, String name) {
        VehicleDTO dto = new VehicleDTO();
        dto.setUid(uid);
        dto.setName(name);
        dto.setUrl("https://www.swapi.tech/api/vehicles/" + uid);
        return dto;
    }

    public static VehicleDetailResponseDTO vehicleDetail(String message) {
        VehicleDetailResponseDTO dto = new VehicleDetailResponseDTO();
        dto.setMessage(message);
        return dto;
    }
}
