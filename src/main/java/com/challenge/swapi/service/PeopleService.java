package com.challenge.swapi.service;

import com.challenge.swapi.client.SwapiClient;
import com.challenge.swapi.dto.PeopleResponseDTO;

import org.springframework.stereotype.Service;

@Service
public class PeopleService {

    private final SwapiClient swapiClient;

    public PeopleService(SwapiClient swapiClient) {
        this.swapiClient = swapiClient;
    }

    public PeopleResponseDTO getPeople() {
        return swapiClient.getPeople();
    }
}