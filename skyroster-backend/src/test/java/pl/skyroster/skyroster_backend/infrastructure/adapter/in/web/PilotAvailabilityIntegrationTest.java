package pl.skyroster.skyroster_backend.infrastructure.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import pl.skyroster.skyroster_backend.TestcontainersConfiguration;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class PilotAvailabilityIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private KeycloakContainer keycloak;
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void availability_shouldReturn401_whenNoToken() throws Exception {
        mockMvc.perform(get("/api/pilots/availability")
                        .param("from", "2026-06-01T08:00:00+02:00")
                        .param("to", "2026-06-01T12:00:00+02:00"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void availability_shouldReturnAllSeededPilots_whenNoConflict() throws Exception {
        String token = getToken("admin", "test1234");
        mockMvc.perform(get("/api/pilots/availability")
                        .param("from", "2026-07-01T08:00:00+02:00")
                        .param("to", "2026-07-01T12:00:00+02:00")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(greaterThanOrEqualTo(4)));
    }

    @Test
    void availability_shouldFilterByAircraftType() throws Exception {
        String token = getToken("admin", "test1234");
        mockMvc.perform(get("/api/pilots/availability")
                        .param("from", "2026-07-01T08:00:00+02:00")
                        .param("to", "2026-07-01T12:00:00+02:00")
                        .param("aircraftTypeId", "b0000000-0000-0000-0000-000000000001")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == 'c0000000-0000-0000-0000-000000000003')]").doesNotExist());
    }

    private String getToken(String username, String password) throws Exception {
        String tokenUrl = keycloak.getAuthServerUrl() + "/realms/skyroster/protocol/openid-connect/token";
        String body = "grant_type=password&client_id=skyroster-frontend&username=" + username + "&password=" + password;
        HttpResponse<String> response = HttpClient.newHttpClient().send(
                HttpRequest.newBuilder()
                        .uri(URI.create(tokenUrl))
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .POST(HttpRequest.BodyPublishers.ofString(body))
                        .build(),
                HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) throw new RuntimeException("Token fetch failed: " + response.body());
        return MAPPER.readTree(response.body()).get("access_token").asText();
    }
}
