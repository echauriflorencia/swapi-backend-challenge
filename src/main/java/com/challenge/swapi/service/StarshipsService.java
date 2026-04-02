package com.challenge.swapi.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import com.challenge.swapi.client.SwapiClient;
import com.challenge.swapi.dto.StarshipDTO;
import com.challenge.swapi.dto.StarshipDetailResponseDTO;
import com.challenge.swapi.dto.StarshipsResponseDTO;
import com.challenge.swapi.exception.ResourceNotFoundException;
import com.challenge.swapi.exception.UpstreamServiceException;

@Service
public class StarshipsService {

	private final SwapiClient swapiClient;

	public StarshipsService(SwapiClient swapiClient) {
		this.swapiClient = swapiClient;
	}

	public StarshipsResponseDTO getStarships(String name, int page, int size) {
		StarshipsResponseDTO response = swapiClient.getStarships(page, size);
		List<StarshipDTO> results = response.getResults();

		if (results == null || results.isEmpty()) {
			return response;
		}

		if (name != null && !name.isBlank()) {
			results = results.stream()
					.filter(s -> s.getName() != null && s.getName().toLowerCase().contains(name.toLowerCase()))
					.collect(Collectors.toList());
		}

		response.setResults(results);
		return response;
	}

	public StarshipDetailResponseDTO getStarshipById(String id) {
        try {
            return swapiClient.getStarshipById(id);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("Starship not found with id: " + id);
        } catch (RestClientException e) {
            throw new UpstreamServiceException("SWAPI request failed while fetching starship with id: " + id, e);
        }
    }
}