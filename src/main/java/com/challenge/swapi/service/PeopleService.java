package com.challenge.swapi.service;

import com.challenge.swapi.client.SwapiClient;
import com.challenge.swapi.dto.PeopleResponseDTO;
import com.challenge.swapi.dto.PersonDTO;
import com.challenge.swapi.dto.PersonDetailResponseDTO;
import com.challenge.swapi.exception.ResourceNotFoundException;
import com.challenge.swapi.exception.UpstreamServiceException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

@Service
public class PeopleService {

	private static final int SWAPI_FETCH_PAGE_SIZE = 50;

	private final SwapiClient swapiClient;

	public PeopleService(SwapiClient swapiClient) {
		this.swapiClient = swapiClient;
	}

	public PeopleResponseDTO getPeople(String id, String name, int page, int size) {
		if (!hasFilters(id, name)) {
			return swapiClient.getPeople(page, size);
		}

		List<PersonDTO> allPeople = fetchAllPeople();
		List<PersonDTO> filtered = allPeople.stream()
				.filter(p -> matchesId(p, id))
				.filter(p -> matchesName(p, name))
				.collect(Collectors.toList());

		PeopleResponseDTO response = new PeopleResponseDTO();
		response.setMessage("ok");
		response.setTotal_records(filtered.size());
		response.setTotal_pages((int) Math.ceil((double) filtered.size() / size));
		response.setResults(paginate(filtered, page, size));
		response.setPrevious(null);
		response.setNext(null);
		return response;
	}

	private List<PersonDTO> fetchAllPeople() {
		List<PersonDTO> all = new ArrayList<>();
		int page = 1;
		int totalPages = 1;

		while (page <= totalPages) {
			PeopleResponseDTO response = swapiClient.getPeople(page, SWAPI_FETCH_PAGE_SIZE);
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

	private boolean matchesId(PersonDTO person, String id) {
		return id == null || id.isBlank() || id.equals(person.getUid());
	}

	private boolean matchesName(PersonDTO person, String name) {
		return name == null || name.isBlank()
				|| person.getName() != null && person.getName().toLowerCase().contains(name.toLowerCase());
	}

	private List<PersonDTO> paginate(List<PersonDTO> results, int page, int size) {
		int fromIndex = (page - 1) * size;
		if (fromIndex >= results.size()) {
			return Collections.emptyList();
		}
		int toIndex = Math.min(fromIndex + size, results.size());
		return results.subList(fromIndex, toIndex);
	}

	 public PersonDetailResponseDTO getPersonById(String id) {
	    	try {
	            return swapiClient.getPersonById(id);
	        } catch (HttpClientErrorException.NotFound e) {
	            throw new ResourceNotFoundException("Person not found with id: " + id);
	        } catch (RestClientException e) {
	            throw new UpstreamServiceException("SWAPI request failed while fetching person with id: " + id, e);
	        }
	    }
}