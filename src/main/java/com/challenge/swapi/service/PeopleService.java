package com.challenge.swapi.service;

import com.challenge.swapi.client.SwapiClient;
import com.challenge.swapi.dto.PeopleResponseDTO;
import com.challenge.swapi.dto.PersonDTO;
import com.challenge.swapi.dto.PersonDetailResponseDTO;
import com.challenge.swapi.exception.ResourceNotFoundException;
import com.challenge.swapi.exception.UpstreamServiceException;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

@Service
public class PeopleService extends AbstractPagedResourceService<PersonDTO, PeopleResponseDTO> {

	private final SwapiClient swapiClient;

	public PeopleService(SwapiClient swapiClient) {
		this.swapiClient = swapiClient;
	}

	public PeopleResponseDTO getPeople(String id, String name, int page, int size) {
		return getResources(id, name, page, size);
	}

	@Override
	protected PeopleResponseDTO fetchPage(int page, int size) {
		return swapiClient.getPeople(page, size);
	}

	@Override
	protected List<PersonDTO> extractResults(PeopleResponseDTO response) {
		return response == null ? null : response.getResults();
	}

	@Override
	protected int extractTotalPages(PeopleResponseDTO response) {
		return response == null ? 0 : response.getTotal_pages();
	}

	@Override
	protected boolean matchesId(PersonDTO person, String id) {
		return id == null || id.isBlank() || id.equals(person.getUid());
	}

	@Override
	protected boolean matchesNameOrTitle(PersonDTO person, String nameOrTitle) {
		return containsIgnoreCase(person.getName(), nameOrTitle);
	}

	@Override
	protected PeopleResponseDTO buildFilteredResponse(List<PersonDTO> pagedResults, int totalRecords, int pageSize) {
		PeopleResponseDTO response = new PeopleResponseDTO();
		response.setMessage("ok");
		response.setTotal_records(totalRecords);
		response.setTotal_pages(calculateTotalPages(totalRecords, pageSize));
		response.setResults(pagedResults);
		response.setPrevious(null);
		response.setNext(null);
		return response;
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