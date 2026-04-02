package com.challenge.swapi.dto;

import lombok.Data;

@Data
public class VehicleDetailDTO {
    private String uid;
    private VehiclePropertiesDTO properties;
}