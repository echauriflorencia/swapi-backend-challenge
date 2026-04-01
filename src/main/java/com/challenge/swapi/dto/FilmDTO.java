package com.challenge.swapi.dto;

import lombok.Data;

@Data
public class FilmDTO {
	private String uid;
	private String description;
	private FilmPropertiesDTO properties;
}