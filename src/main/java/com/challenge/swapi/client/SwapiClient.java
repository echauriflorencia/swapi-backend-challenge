package com.challenge.swapi.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.challenge.swapi.dto.FilmsResponseDTO;
import com.challenge.swapi.dto.PeopleResponseDTO;
import com.challenge.swapi.dto.PersonDetailResponseDTO;

@Component
public class SwapiClient {

	private final RestTemplate restTemplate;
	private final String baseUrl;

	public SwapiClient(@Value("${swapi.base-url:https://www.swapi.tech/api}") String baseUrl) {
		this.restTemplate = new RestTemplate();
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

	public FilmsResponseDTO getFilms() {
		String url = baseUrl + "/films";
		return restTemplate.getForObject(url, FilmsResponseDTO.class);
	}

	private String normalizeBaseUrl(String value) {
		if (value.endsWith("/")) {
			return value.substring(0, value.length() - 1);
		}
		return value;
	}
}