package com.challenge.swapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.challenge.swapi.dto.StarshipDetailResponseDTO;
import com.challenge.swapi.dto.StarshipsResponseDTO;
import com.challenge.swapi.exception.InvalidRequestException;
import com.challenge.swapi.service.StarshipsService;

@RestController
public class StarshipsController {

    private static final int MAX_PAGE_SIZE = 50;

	private final StarshipsService starshipsService;

	public StarshipsController(StarshipsService starshipsService) {
		this.starshipsService = starshipsService;
	}
	
	@GetMapping("/starships")
    public StarshipsResponseDTO getStarships(
			@RequestParam(required = false) String id,
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        validatePagination(page, size);
		return starshipsService.getStarships(id, name, page, size);
    }
	
	@GetMapping("/starships/{id}")
    public StarshipDetailResponseDTO getStarshipById(@PathVariable String id) {
        return starshipsService.getStarshipById(id);
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