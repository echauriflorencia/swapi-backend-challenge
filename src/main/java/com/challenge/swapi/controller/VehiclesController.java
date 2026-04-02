package com.challenge.swapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.challenge.swapi.dto.VehicleDetailResponseDTO;
import com.challenge.swapi.dto.VehiclesResponseDTO;
import com.challenge.swapi.exception.InvalidRequestException;
import com.challenge.swapi.service.VehiclesService;

@RestController
public class VehiclesController {
	
    private static final int MAX_PAGE_SIZE = 50;

	private final VehiclesService vehiclesService;
	
	public VehiclesController(VehiclesService vehiclesService) {
		this.vehiclesService = vehiclesService;
	}
	
	@GetMapping("/vehicles")
	public VehiclesResponseDTO getVehicles(
			@RequestParam(required = false) String id,
			@RequestParam(required = false) String name,
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int size
	) {
        validatePagination(page, size);
		return vehiclesService.getVehicles(id, name, page, size);
	}
	
	@GetMapping("/vehicles/{id}")
	public VehicleDetailResponseDTO getVehicleById(@PathVariable String id) {
		return vehiclesService.getVehicleById(id);
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