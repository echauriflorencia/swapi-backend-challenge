package com.challenge.swapi.dto;

import java.time.Instant;
import java.util.List;

public class UserResponseDTO {

    private final Long id;
    private final String username;
    private final boolean enabled;
    private final List<String> roles;
    private final String createdBy;
    private final Instant createdAt;
    private final String updatedBy;
    private final Instant updatedAt;

    public UserResponseDTO(Long id, String username, boolean enabled, List<String> roles, String createdBy, Instant createdAt, String updatedBy, Instant updatedAt) {
        this.id = id;
        this.username = username;
        this.enabled = enabled;
        this.roles = roles;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedBy = updatedBy;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public List<String> getRoles() {
        return roles;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}