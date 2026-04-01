package com.challenge.swapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.challenge.swapi.dto.FilmsResponseDTO;
import com.challenge.swapi.service.FilmsService;

@RestController
public class FilmsController {
	
	private final FilmsService filmsService;
	
	public FilmsController(FilmsService filmsService) {
		this.filmsService = filmsService;
	}
	
	@GetMapping("/films")
	public FilmsResponseDTO getFilms() {
		return filmsService.getFilms();
	}
}