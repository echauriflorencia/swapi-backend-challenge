package com.challenge.swapi.service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.challenge.swapi.dto.CreateRoleDTO;
import com.challenge.swapi.dto.CreateUserDTO;
import com.challenge.swapi.dto.RoleResponseDTO;
import com.challenge.swapi.dto.UpdateUserDTO;
import com.challenge.swapi.dto.UserResponseDTO;
import com.challenge.swapi.entity.AppRole;
import com.challenge.swapi.entity.AppUser;
import com.challenge.swapi.exception.InvalidRequestException;
import com.challenge.swapi.exception.ResourceNotFoundException;
import com.challenge.swapi.repository.RoleRepository;
import com.challenge.swapi.repository.UserRepository;

@Service
public class UserManagementService {

    private static final String ADMIN_ROLE = "ADMIN";
    private static final String DEFAULT_USER_ROLE = "USER";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserManagementService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> listUsers() {
        return userRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
            .stream()
            .map(this::toUserResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<RoleResponseDTO> listRoles() {
        return roleRepository.findAll(Sort.by(Sort.Direction.ASC, "name"))
            .stream()
            .map(this::toRoleResponse)
            .toList();
    }

    @Transactional
    public UserResponseDTO createUser(CreateUserDTO request) {
        validateUniqueUsername(request.getUsername(), null);

        AppUser user = new AppUser();
        user.setUsername(normalizeUsername(request.getUsername()));
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(request.getEnabled() == null || request.getEnabled());
        user.setRoles(resolveRoles(request.getRoleIds(), true));

        try {
            return toUserResponse(userRepository.save(user));
        } catch (DataIntegrityViolationException ex) {
            throw new InvalidRequestException("Username already exists");
        }
    }

    @Transactional
    public UserResponseDTO updateUser(Long id, UpdateUserDTO request) {
        AppUser user = findUserByIdOrThrow(id);

        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            validateUniqueUsername(request.getUsername(), id);
            user.setUsername(normalizeUsername(request.getUsername()));
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getEnabled() != null) {
            boolean targetEnabled = request.getEnabled();
            if (!targetEnabled) {
                ensureNotRemovingLastActiveAdmin(user);
            }
            user.setEnabled(targetEnabled);
        }

        return toUserResponse(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long id) {
        AppUser user = findUserByIdOrThrow(id);
        ensureNotRemovingLastActiveAdmin(user);
        userRepository.delete(user);
    }

    @Transactional
    public UserResponseDTO assignRole(Long userId, Long roleId) {
        AppUser user = findUserByIdOrThrow(userId);
        AppRole role = findRoleByIdOrThrow(roleId);
        user.getRoles().add(role);
        return toUserResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponseDTO revokeRole(Long userId, Long roleId) {
        AppUser user = findUserByIdOrThrow(userId);
        AppRole role = findRoleByIdOrThrow(roleId);

        if (isAdminRole(role) && user.isEnabled()) {
            ensureNotRemovingLastActiveAdmin(user);
        }

        user.getRoles().removeIf(existingRole -> existingRole.getId().equals(role.getId()));
        return toUserResponse(userRepository.save(user));
    }

    @Transactional
    public RoleResponseDTO createRole(CreateRoleDTO request) {
        String name = normalizeRoleName(request.getName());
        if (roleRepository.existsByNameIgnoreCase(name)) {
            throw new InvalidRequestException("Role already exists");
        }

        AppRole role = new AppRole();
        role.setName(name);
        role.setDescription(request.getDescription());
        try {
            return toRoleResponse(roleRepository.save(role));
        } catch (DataIntegrityViolationException ex) {
            throw new InvalidRequestException("Role already exists");
        }
    }

    private void validateUniqueUsername(String username, Long currentUserId) {
        String normalized = normalizeUsername(username);
        userRepository.findByUsername(normalized).ifPresent(existing -> {
            if (currentUserId == null || !existing.getId().equals(currentUserId)) {
                throw new InvalidRequestException("Username already exists");
            }
        });
    }

    private AppUser findUserByIdOrThrow(Long id) {
        return userRepository.findWithRolesById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    private AppRole findRoleByIdOrThrow(Long id) {
        return roleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));
    }

    private Set<AppRole> resolveRoles(List<Long> roleIds, boolean includeDefaultUserRole) {
        List<Long> effectiveRoleIds = roleIds == null || roleIds.isEmpty()
            ? defaultRoleIds(includeDefaultUserRole)
            : roleIds;

        Set<AppRole> roles = new LinkedHashSet<>();
        for (Long roleId : effectiveRoleIds) {
            roles.add(findRoleByIdOrThrow(roleId));
        }
        return roles;
    }

    private List<Long> defaultRoleIds(boolean includeDefaultUserRole) {
        if (!includeDefaultUserRole) {
            return List.of();
        }

        AppRole userRole = roleRepository.findByNameIgnoreCase(DEFAULT_USER_ROLE)
            .orElseThrow(() -> new ResourceNotFoundException("Default role not found: " + DEFAULT_USER_ROLE));
        return List.of(userRole.getId());
    }

    private void ensureNotRemovingLastActiveAdmin(AppUser user) {
        boolean hasAdminRole = user.getRoles().stream().anyMatch(this::isAdminRole);
        if (user.isEnabled() && hasAdminRole && userRepository.countEnabledUsersWithRole(ADMIN_ROLE) <= 1) {
            throw new InvalidRequestException("At least one enabled ADMIN user must remain");
        }
    }

    private boolean isAdminRole(AppRole role) {
        return role != null && ADMIN_ROLE.equalsIgnoreCase(role.getName());
    }

    private String normalizeUsername(String username) {
        return username == null ? null : username.trim();
    }

    private String normalizeRoleName(String roleName) {
        if (roleName == null || roleName.isBlank()) {
            throw new InvalidRequestException("Role name is required");
        }
        return roleName.trim().toUpperCase(Locale.ROOT);
    }

    private UserResponseDTO toUserResponse(AppUser user) {
        List<String> roles = user.getRoles().stream()
            .map(AppRole::getName)
            .sorted(String::compareToIgnoreCase)
            .toList();

        return new UserResponseDTO(
            user.getId(),
            user.getUsername(),
            user.isEnabled(),
            roles,
            user.getCreatedBy(),
            user.getCreatedAt(),
            user.getUpdatedBy(),
            user.getUpdatedAt()
        );
    }

    private RoleResponseDTO toRoleResponse(AppRole role) {
        return new RoleResponseDTO(
            role.getId(),
            role.getName(),
            role.getDescription(),
            role.getCreatedBy(),
            role.getCreatedAt(),
            role.getUpdatedBy(),
            role.getUpdatedAt()
        );
    }
}