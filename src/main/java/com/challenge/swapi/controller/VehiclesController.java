package com.challenge.swapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.challenge.swapi.dto.VehicleDetailResponseDTO;
import com.challenge.swapi.dto.VehiclesResponseDTO;
import com.challenge.swapi.service.VehiclesService;

@RestController
public class VehiclesController {
	
	private final VehiclesService vehiclesService;
	
	public VehiclesController(VehiclesService vehiclesService) {
		this.vehiclesService = vehiclesService;
	}
	
	@GetMapping("/vehicles")
	public VehiclesResponseDTO getVehicles(
			@RequestParam(required = false) String name,
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int size) {
		return vehiclesService.getVehicles(name, page, size);
	}
	
	@GetMapping("/vehicles/{id}")
	public VehicleDetailResponseDTO getVehicleById(@PathVariable String id) {
		return vehiclesService.getVehicleById(id);
	}
}