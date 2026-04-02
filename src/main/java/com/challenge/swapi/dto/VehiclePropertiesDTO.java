package com.challenge.swapi.dto;

import lombok.Data;

@Data
public class VehiclePropertiesDTO {
    private String name;
    private String model;
    private String manufacturer;
    private String vehicle_class;
    private String crew;
    private String passengers;
}