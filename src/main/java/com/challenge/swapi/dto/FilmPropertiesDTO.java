package com.challenge.swapi.dto;

import lombok.Data;

@Data
public class FilmPropertiesDTO {
	private String title;
	private int episode_id;
	private String producer;
	private String director;
	private String url;
}