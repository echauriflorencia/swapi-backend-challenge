package com.challenge.swapi.dto;

import lombok.Data;

@Data
public class StarshipPropertiesDTO {
    private String name;
    private String model;
    private String manufacturer;
    private String starship_class;
    private String crew;
    private String passengers;
}