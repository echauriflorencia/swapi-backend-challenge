package com.challenge.swapi.service;

import java.util.List;

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
public class FilmsService extends AbstractPagedResourceService<FilmDTO, FilmsResponseDTO> {

	private final SwapiClient swapiClient;

	public FilmsService(SwapiClient swapiClient) {
		this.swapiClient = swapiClient;
	}

	public FilmsResponseDTO getFilms(String id, String title, int page, int size) {
		return getResources(id, title, page, size);
	}

	@Override
	protected FilmsResponseDTO fetchPage(int page, int size) {
		return swapiClient.getFilms(page, size);
	}

	@Override
	protected List<FilmDTO> extractResults(FilmsResponseDTO response) {
		return response == null ? null : response.getResult();
	}

	@Override
	protected int extractTotalPages(FilmsResponseDTO response) {
		return response == null ? 0 : response.getTotal_pages();
	}

	@Override
	protected boolean matchesId(FilmDTO film, String id) {
		return id == null || id.isBlank() || id.equals(film.getUid());
	}

	@Override
	protected boolean matchesNameOrTitle(FilmDTO film, String nameOrTitle) {
		return nameOrTitle == null || nameOrTitle.isBlank()
				|| film.getProperties() != null
				&& film.getProperties().getTitle() != null
				&& film.getProperties().getTitle().toLowerCase().contains(nameOrTitle.toLowerCase());
	}

	@Override
	protected FilmsResponseDTO buildFilteredResponse(List<FilmDTO> pagedResults, int totalRecords, int pageSize) {
		FilmsResponseDTO response = new FilmsResponseDTO();
		response.setMessage("ok");
		response.setTotal_records(totalRecords);
		response.setTotal_pages(calculateTotalPages(totalRecords, pageSize));
		response.setResult(pagedResults);
		response.setPrevious(null);
		response.setNext(null);
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