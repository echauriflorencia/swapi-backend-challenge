package com.challenge.swapi.controller;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.challenge.swapi.dto.AuthLoginRequestDTO;
import com.challenge.swapi.dto.AuthLoginResponseDTO;
import com.challenge.swapi.exception.InvalidRequestException;
import com.challenge.swapi.security.JwtService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public AuthLoginResponseDTO login(@RequestBody AuthLoginRequestDTO request) {
        if (request == null || isBlank(request.getUsername()) || isBlank(request.getPassword())) {
            throw new InvalidRequestException("Username and password are required");
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            String token = jwtService.generateToken(authentication.getName(), authentication.getAuthorities());
            return new AuthLoginResponseDTO(token, "Bearer");
        } catch (AuthenticationException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
