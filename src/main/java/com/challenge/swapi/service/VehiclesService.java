package com.challenge.swapi.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import com.challenge.swapi.client.SwapiClient;
import com.challenge.swapi.dto.VehicleDTO;
import com.challenge.swapi.dto.VehicleDetailResponseDTO;
import com.challenge.swapi.dto.VehiclesResponseDTO;

@Service
public class VehiclesService {

	private final SwapiClient swapiClient;

	public VehiclesService(SwapiClient swapiClient) {
		this.swapiClient = swapiClient;
	}

	public VehiclesResponseDTO getVehicles(String name, int page, int size) {
		VehiclesResponseDTO response = swapiClient.getVehicles(page, size);
		List<VehicleDTO> results = response.getResults();
		if (results == null || results.isEmpty()) {
			return response;
		}

		if (name != null && !name.isBlank()) {
			results = results.stream()
					.filter(v -> v.getName() != null && v.getName().toLowerCase().contains(name.toLowerCase()))
					.collect(Collectors.toList());
		}
		response.setResults(results);
		return response;
	}

	public VehicleDetailResponseDTO getVehicleById(String id) {
		try {
			return swapiClient.getVehicleById(id);
		} catch (RestClientException e) {
			throw new RuntimeException("Vehicle not found");
		}
	}
}