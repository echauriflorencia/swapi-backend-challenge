package com.challenge.swapi.service;

import java.util.ArrayList;
import java.util.Collections;
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

	private static final int SWAPI_FETCH_PAGE_SIZE = 50;

	private final SwapiClient swapiClient;

	public StarshipsService(SwapiClient swapiClient) {
		this.swapiClient = swapiClient;
	}

	public StarshipsResponseDTO getStarships(String id, String name, int page, int size) {
		if (!hasFilters(id, name)) {
			return swapiClient.getStarships(page, size);
		}

		List<StarshipDTO> filtered = fetchAllStarships().stream()
				.filter(s -> matchesId(s, id))
				.filter(s -> matchesName(s, name))
				.collect(Collectors.toList());

		StarshipsResponseDTO response = new StarshipsResponseDTO();
		response.setMessage("ok");
		response.setTotal_records(filtered.size());
		response.setTotal_pages((int) Math.ceil((double) filtered.size() / size));
		response.setResults(paginate(filtered, page, size));
		response.setPrevious(null);
		response.setNext(null);
		return response;
	}

	private List<StarshipDTO> fetchAllStarships() {
		List<StarshipDTO> all = new ArrayList<>();
		int page = 1;
		int totalPages = 1;

		while (page <= totalPages) {
			StarshipsResponseDTO response = swapiClient.getStarships(page, SWAPI_FETCH_PAGE_SIZE);
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

	private boolean matchesId(StarshipDTO starship, String id) {
		return id == null || id.isBlank() || id.equals(starship.getUid());
	}

	private boolean matchesName(StarshipDTO starship, String name) {
		return name == null || name.isBlank()
				|| starship.getName() != null && starship.getName().toLowerCase().contains(name.toLowerCase());
	}

	private List<StarshipDTO> paginate(List<StarshipDTO> results, int page, int size) {
		int fromIndex = (page - 1) * size;
		if (fromIndex >= results.size()) {
			return Collections.emptyList();
		}
		int toIndex = Math.min(fromIndex + size, results.size());
		return results.subList(fromIndex, toIndex);
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