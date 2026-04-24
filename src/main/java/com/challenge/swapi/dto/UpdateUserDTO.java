package com.challenge.swapi.dto;

import jakarta.validation.constraints.Size;

public class UpdateUserDTO {

    @Size(max = 100)
    private String username;

    @Size(min = 6, max = 100)
    private String password;

    private Boolean enabled;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}