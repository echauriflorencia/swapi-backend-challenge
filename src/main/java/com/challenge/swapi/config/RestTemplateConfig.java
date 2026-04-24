package com.challenge.swapi.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(
        RestTemplateBuilder builder,
        @Value("${swapi.connect-timeout:PT5S}") Duration connectTimeout,
        @Value("${swapi.read-timeout:PT10S}") Duration readTimeout
    ) {
        return builder
            .connectTimeout(connectTimeout)
            .readTimeout(readTimeout)
            .build();
    }
}
