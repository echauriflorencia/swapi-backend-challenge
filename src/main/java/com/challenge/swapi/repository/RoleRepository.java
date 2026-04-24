package com.challenge.swapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.challenge.swapi.entity.AppRole;

public interface RoleRepository extends JpaRepository<AppRole, Long> {

    Optional<AppRole> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);
}