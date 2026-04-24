package com.challenge.swapi.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.challenge.swapi.dto.CreateRoleDTO;
import com.challenge.swapi.dto.RoleResponseDTO;
import com.challenge.swapi.service.UserManagementService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/roles")
@Validated
@PreAuthorize("hasRole('ADMIN')")
public class RoleController {

    private final UserManagementService userManagementService;

    public RoleController(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }

    @GetMapping
    public List<RoleResponseDTO> listRoles() {
        return userManagementService.listRoles();
    }

    @PostMapping
    public ResponseEntity<RoleResponseDTO> createRole(@Valid @RequestBody CreateRoleDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userManagementService.createRole(request));
    }
}