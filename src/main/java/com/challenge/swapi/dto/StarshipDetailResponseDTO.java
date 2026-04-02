package com.challenge.swapi.dto;

import lombok.Data;

@Data
public class StarshipDetailResponseDTO {
    private String message;
    private StarshipDetailDTO result;
}