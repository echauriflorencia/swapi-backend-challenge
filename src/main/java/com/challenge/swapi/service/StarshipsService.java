package com.challenge.swapi.service;

import java.util.List;

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
public class StarshipsService extends AbstractPagedResourceService<StarshipDTO, StarshipsResponseDTO> {

	private final SwapiClient swapiClient;

	public StarshipsService(SwapiClient swapiClient) {
		this.swapiClient = swapiClient;
	}

	public StarshipsResponseDTO getStarships(String id, String name, int page, int size) {
		return getResources(id, name, page, size);
	}

	@Override
	protected StarshipsResponseDTO fetchPage(int page, int size) {
		return swapiClient.getStarships(page, size);
	}

	@Override
	protected List<StarshipDTO> extractResults(StarshipsResponseDTO response) {
		return response == null ? null : response.getResults();
	}

	@Override
	protected int extractTotalPages(StarshipsResponseDTO response) {
		return response == null ? 0 : response.getTotal_pages();
	}

	@Override
	protected boolean matchesId(StarshipDTO starship, String id) {
		return id == null || id.isBlank() || id.equals(starship.getUid());
	}

	@Override
	protected boolean matchesNameOrTitle(StarshipDTO starship, String nameOrTitle) {
		return containsIgnoreCase(starship.getName(), nameOrTitle);
	}

	@Override
	protected StarshipsResponseDTO buildFilteredResponse(List<StarshipDTO> pagedResults, int totalRecords, int pageSize) {
		StarshipsResponseDTO response = new StarshipsResponseDTO();
		response.setMessage("ok");
		response.setTotal_records(totalRecords);
		response.setTotal_pages(calculateTotalPages(totalRecords, pageSize));
		response.setResults(pagedResults);
		response.setPrevious(null);
		response.setNext(null);
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