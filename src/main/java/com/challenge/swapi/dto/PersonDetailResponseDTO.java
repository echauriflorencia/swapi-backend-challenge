package com.challenge.swapi.dto;

import lombok.Data;

@Data
public class PersonDetailResponseDTO {
	private String message;
	private PersonDetailDTO result;
}