package com.challenge.swapi.dto;

import lombok.Data;
import java.util.List;

@Data
public class PeopleResponseDTO {
	private String message;
	private int total_records;
	private int total_pages;
	private String previous;
	private String next;
	private List<PersonDTO> results;
}