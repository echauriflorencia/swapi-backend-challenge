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

	private static final int SWAPI_FETCH_PAGE_SIZE = 50;

	private final SwapiClient swapiClient;

	public FilmsService(SwapiClient swapiClient) {
		this.swapiClient = swapiClient;
	}

	public FilmsResponseDTO getFilms(String id, String title, int page, int size) {
		if (!hasFilters(id, title)) {
			return swapiClient.getFilms(page, size);
		}

		List<FilmDTO> filtered = fetchAllFilms().stream()
				.filter(f -> matchesId(f, id))
				.filter(f -> matchesTitle(f, title))
				.collect(Collectors.toList());

		FilmsResponseDTO response = new FilmsResponseDTO();
		response.setMessage("ok");
		response.setTotal_records(filtered.size());
		response.setTotal_pages((int) Math.ceil((double) filtered.size() / size));
		response.setResult(paginate(filtered, page, size));
		response.setPrevious(null);
		response.setNext(null);
		return response;
	}

	private List<FilmDTO> fetchAllFilms() {
		List<FilmDTO> all = new ArrayList<>();
		int page = 1;
		int totalPages = 1;

		while (page <= totalPages) {
			FilmsResponseDTO response = swapiClient.getFilms(page, SWAPI_FETCH_PAGE_SIZE);
			if (response == null || response.getResult() == null || response.getResult().isEmpty()) {
				break;
			}
			all.addAll(response.getResult());
			totalPages = response.getTotal_pages() > 0 ? response.getTotal_pages() : page;
			page++;
		}

		return all;
	}

	private boolean hasFilters(String id, String title) {
		return id != null && !id.isBlank() || title != null && !title.isBlank();
	}

	private boolean matchesId(FilmDTO film, String id) {
		return id == null || id.isBlank() || id.equals(film.getUid());
	}

	private boolean matchesTitle(FilmDTO film, String title) {
		return title == null || title.isBlank()
				|| film.getProperties() != null
				&& film.getProperties().getTitle() != null
				&& film.getProperties().getTitle().toLowerCase().contains(title.toLowerCase());
	}

	private List<FilmDTO> paginate(List<FilmDTO> results, int page, int size) {
		int fromIndex = (page - 1) * size;
		if (fromIndex >= results.size()) {
			return Collections.emptyList();
		}
		int toIndex = Math.min(fromIndex + size, results.size());
		return results.subList(fromIndex, toIndex);
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