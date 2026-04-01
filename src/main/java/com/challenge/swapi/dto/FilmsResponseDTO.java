package com.challenge.swapi.dto;

import java.util.List;

import lombok.Data;
@Data
public class FilmsResponseDTO {
    private String message;
    private List<FilmDTO> result;
}	