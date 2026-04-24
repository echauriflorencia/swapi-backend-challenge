package com.challenge.swapi.integration;

import static com.challenge.swapi.fixtures.TestFixtures.filmDetail;
import static com.challenge.swapi.fixtures.TestFixtures.peopleResponse;
import static com.challenge.swapi.fixtures.TestFixtures.person;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.challenge.swapi.dto.AuthLoginRequestDTO;
import com.challenge.swapi.entity.AppRole;
import com.challenge.swapi.entity.AppUser;
import com.challenge.swapi.exception.ResourceNotFoundException;
import com.challenge.swapi.exception.UpstreamServiceException;
import com.challenge.swapi.repository.RoleRepository;
import com.challenge.swapi.repository.UserRepository;
import com.challenge.swapi.service.FilmsService;
import com.challenge.swapi.service.PeopleService;
import com.challenge.swapi.service.StarshipsService;
import com.challenge.swapi.service.VehiclesService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@AutoConfigureMockMvc
class ApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private PeopleService peopleService;

    @MockitoBean
    private FilmsService filmsService;

    @MockitoBean
    private StarshipsService starshipsService;

    @MockitoBean
    private VehiclesService vehiclesService;

    @Test
    void protectedEndpointRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/people"))
            .andExpect(status().is4xxClientError());
    }

    @Test
    void loginReturnsJwtToken() throws Exception {
        AuthLoginRequestDTO request = loginRequest("swapi", "swapi123");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").isNotEmpty())
            .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    void peopleEndpointSupportsPaginationAndFiltersWithValidToken() throws Exception {
        when(peopleService.getPeople(isNull(), eq("luke"), eq(2), eq(5)))
            .thenReturn(peopleResponse(person("1", "Luke Skywalker")));

        mockMvc.perform(get("/people")
                .header("Authorization", bearerToken())
                .param("name", "luke")
                .param("page", "2")
                .param("size", "5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.results[0].name").value("Luke Skywalker"));

        verify(peopleService).getPeople(null, "luke", 2, 5);
    }

    @Test
    void peopleEndpointSupportsCombinedIdAndNameFiltersWithValidToken() throws Exception {
        when(peopleService.getPeople(eq("1"), eq("luke"), eq(1), eq(5)))
            .thenReturn(peopleResponse(person("1", "Luke Skywalker")));

        mockMvc.perform(get("/people")
                .header("Authorization", bearerToken())
                .param("id", "1")
                .param("name", "luke")
                .param("page", "1")
                .param("size", "5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.results[0].uid").value("1"));

        verify(peopleService).getPeople("1", "luke", 1, 5);
    }

    @Test
    void invalidPaginationReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/people")
                .header("Authorization", bearerToken())
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$").value("Parameter 'page' must be greater than or equal to 1"));
    }

    @Test
    void nonexistentIdReturnsNotFound() throws Exception {
        when(peopleService.getPersonById("999"))
            .thenThrow(new ResourceNotFoundException("Person not found with id: 999"));

        mockMvc.perform(get("/people/999")
                .header("Authorization", bearerToken()))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$").value("Person not found with id: 999"));
    }

    @Test
    void upstreamFailuresReturnServiceUnavailable() throws Exception {
        when(filmsService.getFilmById("1"))
            .thenThrow(new UpstreamServiceException("SWAPI unavailable", new RuntimeException("down")));

        mockMvc.perform(get("/films/1")
                .header("Authorization", bearerToken()))
            .andExpect(status().isServiceUnavailable())
            .andExpect(jsonPath("$").value("SWAPI unavailable"));
    }

    @Test
    void protectedEndpointAcceptsTokenAndDelegatesToService() throws Exception {
        when(filmsService.getFilmById("1")).thenReturn(filmDetail("ok"));

        mockMvc.perform(get("/films/1")
                .header("Authorization", bearerToken()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("ok"));

        verify(filmsService).getFilmById("1");
    }

    @Test
    void adminCanListRoles() throws Exception {
        mockMvc.perform(get("/roles")
                .header("Authorization", bearerToken()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[?(@.name == 'ADMIN')]").isArray())
            .andExpect(jsonPath("$[?(@.name == 'USER')]").isArray());
    }

    @Test
    void nonAdminUserCannotAccessRolesEndpoint() throws Exception {
        String username = "only-user-" + System.nanoTime();
        createUserWithSingleRole(username, "swapi123", "USER");

        mockMvc.perform(get("/roles")
                .header("Authorization", bearerToken(username, "swapi123")))
            .andExpect(status().isForbidden());
    }

    @Test
    void existingStarshipsEndpointStillWorksWithValidToken() throws Exception {
        mockMvc.perform(get("/starships")
                .header("Authorization", bearerToken()))
            .andExpect(status().isOk());

        verify(starshipsService).getStarships(null, null, 1, 10);
    }

    @Test
    void existingVehiclesEndpointStillWorksWithValidToken() throws Exception {
        mockMvc.perform(get("/vehicles")
                .header("Authorization", bearerToken()))
            .andExpect(status().isOk());

        verify(vehiclesService).getVehicles(null, null, 1, 10);
    }

    private String bearerToken() throws Exception {
        return bearerToken("swapi", "swapi123");
    }

    private String bearerToken(String username, String password) throws Exception {
        AuthLoginRequestDTO request = loginRequest(username, password);

        MvcResult result = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andReturn();

        String token = objectMapper.readTree(result.getResponse().getContentAsString()).get("token").asText();
        return "Bearer " + token;
    }

    private void createUserWithSingleRole(String username, String rawPassword, String roleName) {
        AppRole role = roleRepository.findByNameIgnoreCase(roleName)
            .orElseThrow(() -> new IllegalStateException("Role not found in test: " + roleName));

        AppUser user = new AppUser();
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setEnabled(true);
        user.getRoles().add(role);
        userRepository.save(user);
    }

    private AuthLoginRequestDTO loginRequest(String username, String password) {
        AuthLoginRequestDTO request = new AuthLoginRequestDTO();
        request.setUsername(username);
        request.setPassword(password);
        return request;
    }
}
