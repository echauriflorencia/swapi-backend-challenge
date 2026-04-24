package com.challenge.swapi.service;

import java.util.List;

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
public class VehiclesService extends AbstractPagedResourceService<VehicleDTO, VehiclesResponseDTO> {

	private final SwapiClient swapiClient;

	public VehiclesService(SwapiClient swapiClient) {
		this.swapiClient = swapiClient;
	}

	public VehiclesResponseDTO getVehicles(String id, String name, int page, int size) {
		return getResources(id, name, page, size);
	}

	@Override
	protected VehiclesResponseDTO fetchPage(int page, int size) {
		return swapiClient.getVehicles(page, size);
	}

	@Override
	protected List<VehicleDTO> extractResults(VehiclesResponseDTO response) {
		return response == null ? null : response.getResults();
	}

	@Override
	protected int extractTotalPages(VehiclesResponseDTO response) {
		return response == null ? 0 : response.getTotal_pages();
	}

	@Override
	protected boolean matchesId(VehicleDTO vehicle, String id) {
		return id == null || id.isBlank() || id.equals(vehicle.getUid());
	}

	@Override
	protected boolean matchesNameOrTitle(VehicleDTO vehicle, String nameOrTitle) {
		return containsIgnoreCase(vehicle.getName(), nameOrTitle);
	}

	@Override
	protected VehiclesResponseDTO buildFilteredResponse(List<VehicleDTO> pagedResults, int totalRecords, int pageSize) {
		VehiclesResponseDTO response = new VehiclesResponseDTO();
		response.setMessage("ok");
		response.setTotal_records(totalRecords);
		response.setTotal_pages(calculateTotalPages(totalRecords, pageSize));
		response.setResults(pagedResults);
		response.setPrevious(null);
		response.setNext(null);
		return response;
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