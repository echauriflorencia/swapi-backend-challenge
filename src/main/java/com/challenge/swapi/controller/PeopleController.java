package com.challenge.swapi.controller;

import com.challenge.swapi.dto.PeopleResponseDTO;
import com.challenge.swapi.dto.PersonDetailResponseDTO;
import com.challenge.swapi.service.PeopleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PeopleController {

	private final PeopleService peopleService;

	public PeopleController(PeopleService peopleService) {
		this.peopleService = peopleService;
	}

	@GetMapping("/people")
	public PeopleResponseDTO getPeople(@RequestParam(required = false) String name) {
		return peopleService.getPeople(name);
	}

	@GetMapping("/people/{id}")
	public PersonDetailResponseDTO getPersonById(@PathVariable String id) {
		return peopleService.getPersonById(id);
	}
}