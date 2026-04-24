package com.challenge.swapi.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RestTemplateConfigTest {

    @Mock
    private RestTemplateBuilder builder;

    @Test
    void restTemplateAppliesConfiguredTimeouts() {
        RestTemplateConfig config = new RestTemplateConfig();
        RestTemplate expected = new RestTemplate();

        when(builder.connectTimeout(Duration.ofSeconds(3))).thenReturn(builder);
        when(builder.readTimeout(Duration.ofSeconds(7))).thenReturn(builder);
        when(builder.build()).thenReturn(expected);

        RestTemplate restTemplate = config.restTemplate(
            builder,
            Duration.ofSeconds(3),
            Duration.ofSeconds(7)
        );

        assertThat(restTemplate).isSameAs(expected);
        verify(builder).connectTimeout(Duration.ofSeconds(3));
        verify(builder).readTimeout(Duration.ofSeconds(7));
        verify(builder).build();
    }
}
