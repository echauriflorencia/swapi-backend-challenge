package com.challenge.swapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.challenge.swapi.dto.FilmDetailResponseDTO;
import com.challenge.swapi.dto.FilmsResponseDTO;
import com.challenge.swapi.service.FilmsService;

@RestController
public class FilmsController {

	private final FilmsService filmsService;

	public FilmsController(FilmsService filmsService) {
		this.filmsService = filmsService;
	}

	@GetMapping("/films")
	public FilmsResponseDTO getFilms(@RequestParam(required = false) String title,
			@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {

		return filmsService.getFilms(title, page, size);
	}

	@GetMapping("/films/{id}")
	public FilmDetailResponseDTO getFilmById(@PathVariable String id) {
		return filmsService.getFilmById(id);
	}

}