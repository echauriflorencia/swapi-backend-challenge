package com.challenge.swapi.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

class JwtServiceTest {

    @Test
    void generateTokenAndValidateWithProperSecret() {
        JwtService jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "jwtSecret", "changeit-changeit-changeit-changeit");
        ReflectionTestUtils.setField(jwtService, "jwtExpirationMinutes", 60L);

        String token = jwtService.generateToken("swapi");
        UserDetails userDetails = User.withUsername("swapi").password("x").authorities("USER").build();

        assertThat(token).isNotBlank();
        assertThat(jwtService.extractUsername(token)).isEqualTo("swapi");
        assertThat(jwtService.isTokenValid(token, userDetails)).isTrue();
    }

    @Test
    void generateTokenFailsWhenSecretIsTooShort() {
        JwtService jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "jwtSecret", "short-secret");
        ReflectionTestUtils.setField(jwtService, "jwtExpirationMinutes", 60L);

        assertThatThrownBy(() -> jwtService.generateToken("swapi"))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("JWT secret must be at least 32 bytes long");
    }
}
