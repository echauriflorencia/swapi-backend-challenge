package com.challenge.swapi.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.challenge.swapi.dto.FilmDetailResponseDTO;
import com.challenge.swapi.dto.FilmsResponseDTO;
import com.challenge.swapi.dto.PeopleResponseDTO;
import com.challenge.swapi.dto.PersonDetailResponseDTO;
import com.challenge.swapi.dto.StarshipDetailResponseDTO;
import com.challenge.swapi.dto.StarshipsResponseDTO;
import com.challenge.swapi.dto.VehicleDetailResponseDTO;
import com.challenge.swapi.dto.VehiclesResponseDTO;

@Component
public class SwapiClient {

	private final RestTemplate restTemplate;
	private final String baseUrl;

	public SwapiClient(
		RestTemplate restTemplate,
		@Value("${swapi.base-url:https://www.swapi.tech/api}") String baseUrl
	) {
		this.restTemplate = restTemplate;
		this.baseUrl = normalizeBaseUrl(baseUrl);
	}

	public PeopleResponseDTO getPeople(int page, int size) {
		String url = baseUrl + "/people?page=" + page + "&limit=" + size;
		return restTemplate.getForObject(url, PeopleResponseDTO.class);
	}

	public PersonDetailResponseDTO getPersonById(String id) {
		String url = baseUrl + "/people/" + id;
		return restTemplate.getForObject(url, PersonDetailResponseDTO.class);
	}

	public FilmsResponseDTO getFilms(int page, int size) {
		String url = baseUrl + "/films?page=" + page + "&limit=" + size;
		return restTemplate.getForObject(url, FilmsResponseDTO.class);
	}

	public FilmDetailResponseDTO getFilmById(String id) {
		String url = baseUrl + "/films/" + id;
		return restTemplate.getForObject(url, FilmDetailResponseDTO.class);
	}
	
	public StarshipsResponseDTO getStarships(int page, int size) {
		String url = baseUrl + "/starships?page=" + page + "&limit=" + size;
		return restTemplate.getForObject(url, StarshipsResponseDTO.class);
	}

	public StarshipDetailResponseDTO getStarshipById(String id) {
		String url = baseUrl + "/starships/" + id;
		return restTemplate.getForObject(url, StarshipDetailResponseDTO.class);
	}
	
	public VehiclesResponseDTO getVehicles(int page, int size) {
		String url = baseUrl + "/vehicles?page=" + page + "&limit=" + size;
		return restTemplate.getForObject(url, VehiclesResponseDTO.class);
	}

	public VehicleDetailResponseDTO getVehicleById(String id) {
		String url = baseUrl + "/vehicles/" + id;
		return restTemplate.getForObject(url, VehicleDetailResponseDTO.class);
	}
	
	private String normalizeBaseUrl(String value) {
		if (value.endsWith("/")) {
			return value.substring(0, value.length() - 1);
		}
		return value;
	}
}