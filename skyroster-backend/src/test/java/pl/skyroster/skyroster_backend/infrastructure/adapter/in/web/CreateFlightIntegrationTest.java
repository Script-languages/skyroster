package pl.skyroster.skyroster_backend.infrastructure.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pl.skyroster.skyroster_backend.TestcontainersConfiguration;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class CreateFlightIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private KeycloakContainer keycloak;
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void createFlight_shouldReturn401_whenNoToken() throws Exception {
        mockMvc.perform(post("/api/flights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createFlight_shouldReturn201_happyPath() throws Exception {
        String token = getToken("admin", "test1234");
        String body = """
                {
                  "aircraftId": "e0000000-0000-0000-0000-000000000004",
                  "pilotId": "c0000000-0000-0000-0000-000000000004",
                  "startDateTime": "2026-09-01T08:00:00+02:00",
                  "endDateTime": "2026-09-01T10:00:00+02:00",
                  "startAirportId": "a0000000-0000-0000-0000-000000000001",
                  "endAirportId": "a0000000-0000-0000-0000-000000000002",
                  "description": "Test flight"
                }
                """;
        mockMvc.perform(post("/api/flights")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.aircraft.id").value("e0000000-0000-0000-0000-000000000004"));
    }

    @Test
    void createFlight_shouldReturn400_whenStartAfterEnd() throws Exception {
        String token = getToken("admin", "test1234");
        String body = """
                {
                  "aircraftId": "e0000000-0000-0000-0000-000000000004",
                  "pilotId": "c0000000-0000-0000-0000-000000000004",
                  "startDateTime": "2026-09-01T10:00:00+02:00",
                  "endDateTime": "2026-09-01T08:00:00+02:00",
                  "startAirportId": "a0000000-0000-0000-0000-000000000001"
                }
                """;
        mockMvc.perform(post("/api/flights")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createFlight_shouldReturn404_whenStartAirportUnknown() throws Exception {
        String token = getToken("admin", "test1234");
        String body = """
                {
                  "aircraftId": "e0000000-0000-0000-0000-000000000004",
                  "pilotId": "c0000000-0000-0000-0000-000000000004",
                  "startDateTime": "2026-09-02T08:00:00+02:00",
                  "endDateTime": "2026-09-02T10:00:00+02:00",
                  "startAirportId": "ffffffff-ffff-ffff-ffff-ffffffffffff"
                }
                """;
        mockMvc.perform(post("/api/flights")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    void createFlight_shouldReturn409_whenAircraftConflict() throws Exception {
        String token = getToken("admin", "test1234");
        String body = """
                {
                  "aircraftId": "e0000000-0000-0000-0000-000000000001",
                  "pilotId": "c0000000-0000-0000-0000-000000000004",
                  "startDateTime": "2026-05-09T11:00:00+02:00",
                  "endDateTime": "2026-05-09T12:00:00+02:00",
                  "startAirportId": "a0000000-0000-0000-0000-000000000001"
                }
                """;
        mockMvc.perform(post("/api/flights")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict());
    }

    @Test
    void createFlight_shouldReturn422_whenRuleViolated() throws Exception {
        String token = getToken("admin", "test1234");
        String body = """
                {
                  "aircraftId": "e0000000-0000-0000-0000-000000000004",
                  "pilotId": "c0000000-0000-0000-0000-000000000004",
                  "startDateTime": "2026-10-01T06:00:00+02:00",
                  "endDateTime": "2026-10-01T18:00:00+02:00",
                  "startAirportId": "a0000000-0000-0000-0000-000000000001"
                }
                """;
        mockMvc.perform(post("/api/flights")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.violations").isArray())
                .andExpect(jsonPath("$.violations.length()").value(greaterThanOrEqualTo(1)));
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
