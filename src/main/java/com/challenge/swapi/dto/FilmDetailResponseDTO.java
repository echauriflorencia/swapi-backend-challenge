package com.challenge.swapi.dto;

import lombok.Data;

@Data
public class FilmDetailResponseDTO {
    private String message;
    private FilmDetailDTO result;
}