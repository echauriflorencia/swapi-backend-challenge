package com.challenge.swapi.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import com.challenge.swapi.client.SwapiClient;
import com.challenge.swapi.dto.VehicleDTO;
import com.challenge.swapi.dto.VehicleDetailResponseDTO;
import com.challenge.swapi.dto.VehiclesResponseDTO;
import com.challenge.swapi.exception.ResourceNotFoundException;
import com.challenge.swapi.exception.UpstreamServiceException;

@Service
public class VehiclesService {

	private static final int SWAPI_FETCH_PAGE_SIZE = 50;

	private final SwapiClient swapiClient;

	public VehiclesService(SwapiClient swapiClient) {
		this.swapiClient = swapiClient;
	}

	public VehiclesResponseDTO getVehicles(String id, String name, int page, int size) {
		if (!hasFilters(id, name)) {
			return swapiClient.getVehicles(page, size);
		}

		List<VehicleDTO> filtered = fetchAllVehicles().stream()
				.filter(v -> matchesId(v, id))
				.filter(v -> matchesName(v, name))
				.collect(Collectors.toList());

		VehiclesResponseDTO response = new VehiclesResponseDTO();
		response.setMessage("ok");
		response.setTotal_records(filtered.size());
		response.setTotal_pages((int) Math.ceil((double) filtered.size() / size));
		response.setResults(paginate(filtered, page, size));
		response.setPrevious(null);
		response.setNext(null);
		return response;
	}

	private List<VehicleDTO> fetchAllVehicles() {
		List<VehicleDTO> all = new ArrayList<>();
		int page = 1;
		int totalPages = 1;

		while (page <= totalPages) {
			VehiclesResponseDTO response = swapiClient.getVehicles(page, SWAPI_FETCH_PAGE_SIZE);
			if (response == null || response.getResults() == null || response.getResults().isEmpty()) {
				break;
			}
			all.addAll(response.getResults());
			totalPages = response.getTotal_pages() > 0 ? response.getTotal_pages() : page;
			page++;
		}

		return all;
	}

	private boolean hasFilters(String id, String name) {
		return id != null && !id.isBlank() || name != null && !name.isBlank();
	}

	private boolean matchesId(VehicleDTO vehicle, String id) {
		return id == null || id.isBlank() || id.equals(vehicle.getUid());
	}

	private boolean matchesName(VehicleDTO vehicle, String name) {
		return name == null || name.isBlank()
				|| vehicle.getName() != null && vehicle.getName().toLowerCase().contains(name.toLowerCase());
	}

	private List<VehicleDTO> paginate(List<VehicleDTO> results, int page, int size) {
		int fromIndex = (page - 1) * size;
		if (fromIndex >= results.size()) {
			return Collections.emptyList();
		}
		int toIndex = Math.min(fromIndex + size, results.size());
		return results.subList(fromIndex, toIndex);
	}

	public VehicleDetailResponseDTO getVehicleById(String id) {
        try {
            return swapiClient.getVehicleById(id);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("Vehicle not found with id: " + id);
        } catch (RestClientException e) {
            throw new UpstreamServiceException("SWAPI request failed while fetching vehicle with id: " + id, e);
        }
    }
}