package com.challenge.swapi.service;

import com.challenge.swapi.client.SwapiClient;
import com.challenge.swapi.dto.PeopleResponseDTO;
import com.challenge.swapi.dto.PersonDTO;
import com.challenge.swapi.dto.PersonDetailResponseDTO;
import com.challenge.swapi.exception.ResourceNotFoundException;
import com.challenge.swapi.exception.UpstreamServiceException;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

@Service
public class PeopleService {

	private final SwapiClient swapiClient;

	public PeopleService(SwapiClient swapiClient) {
		this.swapiClient = swapiClient;
	}

	public PeopleResponseDTO getPeople(String name, int page, int size) {
		PeopleResponseDTO response = swapiClient.getPeople(page, size);

		if (name == null || name.isEmpty()) {
			return response;
		}

		List<PersonDTO> filtered = response.getResults()
				.stream()
				.filter(p -> p.getName().toLowerCase().contains(name.toLowerCase()))
				.collect(Collectors.toList());

		response.setResults(filtered);
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