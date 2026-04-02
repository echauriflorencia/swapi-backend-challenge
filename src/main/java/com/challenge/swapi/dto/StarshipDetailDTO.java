package com.challenge.swapi.dto;

import lombok.Data;

@Data
public class StarshipDetailDTO {
    private String uid;
    private String description;
    private StarshipPropertiesDTO properties;
}