package com.challenge.swapi.dto;

import lombok.Data;

@Data
public class VehicleDetailResponseDTO {
    private String message;
    private VehicleDetailDTO result;
}