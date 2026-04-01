package com.challenge.swapi.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.challenge.swapi.dto.PeopleResponseDTO;
import com.challenge.swapi.dto.PersonDetailResponseDTO;

@Component
public class SwapiClient {

	private final RestTemplate restTemplate;

	public SwapiClient() {
		this.restTemplate = new RestTemplate();
	}

	public PeopleResponseDTO getPeople() {
		String url = "https://www.swapi.tech/api/people";
		return restTemplate.getForObject(url, PeopleResponseDTO.class);
	}

	public PersonDetailResponseDTO getPersonById(String id) {
		String url = "https://www.swapi.tech/api/people/" + id;
		return restTemplate.getForObject(url, PersonDetailResponseDTO.class);
	}
}