package com.challenge.swapi.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import com.challenge.swapi.client.SwapiClient;
import com.challenge.swapi.dto.FilmDTO;
import com.challenge.swapi.dto.FilmDetailResponseDTO;
import com.challenge.swapi.dto.FilmsResponseDTO;
import com.challenge.swapi.exception.ResourceNotFoundException;
import com.challenge.swapi.exception.UpstreamServiceException;

@Service
public class FilmsService {

	private final SwapiClient swapiClient;

	public FilmsService(SwapiClient swapiClient) {
		this.swapiClient = swapiClient;
	}

	public FilmsResponseDTO getFilms(String title, int page, int size) {
		FilmsResponseDTO response = swapiClient.getFilms(page, size);

		if (response == null || response.getResult() == null) {
			return response;
		}

		List<FilmDTO> results = new ArrayList<>(response.getResult());
		if (title != null && !title.isEmpty()) {
			String normalizedTitle = title.toLowerCase();
			results = results.stream()
					.filter(f -> f.getProperties().getTitle() != null
							&& f.getProperties().getTitle().toLowerCase().contains(normalizedTitle))
					.collect(Collectors.toList());
		}

		if (size > 0 && results.size() > size) {
			int fromIndex = Math.max(0, (page - 1) * size);
			if (fromIndex >= results.size()) {
				results = Collections.emptyList();
			} else {
				int toIndex = Math.min(fromIndex + size, results.size());
				results = results.subList(fromIndex, toIndex);
			}
		}

		response.setResult(results);
		return response;
	}

	public FilmDetailResponseDTO getFilmById(String id) {
        try {
            return swapiClient.getFilmById(id);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("Film not found with id: " + id);
        } catch (RestClientException e) {
            throw new UpstreamServiceException("SWAPI request failed while fetching film with id: " + id, e);
        }
    }
}