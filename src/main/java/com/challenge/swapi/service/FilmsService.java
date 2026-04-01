package com.challenge.swapi.service;

import org.springframework.stereotype.Service;

import com.challenge.swapi.client.SwapiClient;
import com.challenge.swapi.dto.FilmsResponseDTO;

@Service
public class FilmsService {

	private final SwapiClient swapiClient;
	
	public FilmsService(SwapiClient swapiClient) {
		this.swapiClient = swapiClient;
	}
	
	public FilmsResponseDTO getFilms() {
		return swapiClient.getFilms();
	}
}