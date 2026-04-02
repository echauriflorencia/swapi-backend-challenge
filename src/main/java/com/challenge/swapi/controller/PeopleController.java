package com.challenge.swapi.controller;

import com.challenge.swapi.dto.PeopleResponseDTO;
import com.challenge.swapi.dto.PersonDetailResponseDTO;
import com.challenge.swapi.exception.InvalidRequestException;
import com.challenge.swapi.service.PeopleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PeopleController {

    private static final int MAX_PAGE_SIZE = 50;

	private final PeopleService peopleService;

	public PeopleController(PeopleService peopleService) {
		this.peopleService = peopleService;
	}

	@GetMapping("/people")
	public PeopleResponseDTO getPeople(
			@RequestParam(required = false) String id,
			@RequestParam(required = false) String name,
			@RequestParam(defaultValue = "1") int page, 
			@RequestParam(defaultValue = "10") int size
	) {
		validatePagination(page, size);
		return peopleService.getPeople(id, name, page, size);
	}

	@GetMapping("/people/{id}")
	public PersonDetailResponseDTO getPersonById(@PathVariable String id) {
		return peopleService.getPersonById(id);
	}
	
	private void validatePagination(int page, int size) {
        if (page < 1) {
            throw new InvalidRequestException("Parameter 'page' must be greater than or equal to 1");
        }
        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw new InvalidRequestException(
                    "Parameter 'size' must be between 1 and " + MAX_PAGE_SIZE
            );
        }
    }
}