package com.challenge.swapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.challenge.swapi.entity.AppUser;

public interface UserRepository extends JpaRepository<AppUser, Long> {

    boolean existsByUsernameIgnoreCase(String username);

    Optional<AppUser> findByUsername(String username);

    @EntityGraph(attributePaths = "roles")
    Optional<AppUser> findWithRolesByUsername(String username);

    @EntityGraph(attributePaths = "roles")
    Optional<AppUser> findWithRolesById(Long id);

    @Query("select count(distinct u) from AppUser u join u.roles r where u.enabled = true and upper(r.name) = upper(:roleName)")
    long countEnabledUsersWithRole(@Param("roleName") String roleName);
}