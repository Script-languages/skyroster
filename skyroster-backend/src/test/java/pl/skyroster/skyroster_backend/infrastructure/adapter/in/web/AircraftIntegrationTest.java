package pl.skyroster.skyroster_backend.infrastructure.adapter.in.web;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class AircraftIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private KeycloakContainer keycloak;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // Seed UUIDs from V2 migration
    private static final String TYPE_B738_ID = "b0000000-0000-0000-0000-000000000001";
    private static final String BASE_EPWA_ID = "a0000000-0000-0000-0000-000000000001";

    @Test
    void addAircraft_shouldReturn201_whenAdminWithValidData() throws Exception {
        String token = getToken("admin", "test1234");
        String body = """
                {
                    "registrationNumber": "SP-LRA",
                    "aircraftTypeId": "%s",
                    "operationalBaseId": "%s"
                }
                """.formatted(TYPE_B738_ID, BASE_EPWA_ID);

        mockMvc.perform(post("/api/aircraft")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.registrationNumber").value("SP-LRA"))
                .andExpect(jsonPath("$.aircraftType.icaoCode").value("B738"))
                .andExpect(jsonPath("$.operationalBase.icaoCode").value("EPWA"))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void addAircraft_shouldReturn409_whenDuplicateRegistration() throws Exception {
        String token = getToken("admin", "test1234");
        String body = """
                {
                    "registrationNumber": "SP-DUP",
                    "aircraftTypeId": "%s",
                    "operationalBaseId": "%s"
                }
                """.formatted(TYPE_B738_ID, BASE_EPWA_ID);

        // First request — should succeed
        mockMvc.perform(post("/api/aircraft")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        // Second request — should conflict
        mockMvc.perform(post("/api/aircraft")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void addAircraft_shouldReturn400_whenAircraftTypeNotFound() throws Exception {
        String token = getToken("admin", "test1234");
        String body = """
                {
                    "registrationNumber": "SP-BAD",
                    "aircraftTypeId": "00000000-0000-0000-0000-000000000099",
                    "operationalBaseId": "%s"
                }
                """.formatted(BASE_EPWA_ID);

        mockMvc.perform(post("/api/aircraft")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void addAircraft_shouldReturn400_whenOperationalBaseNotFound() throws Exception {
        String token = getToken("admin", "test1234");
        String body = """
                {
                    "registrationNumber": "SP-BAD2",
                    "aircraftTypeId": "%s",
                    "operationalBaseId": "00000000-0000-0000-0000-000000000099"
                }
                """.formatted(TYPE_B738_ID);

        mockMvc.perform(post("/api/aircraft")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void addAircraft_shouldReturn401_whenNoToken() throws Exception {
        String body = """
                {
                    "registrationNumber": "SP-NO",
                    "aircraftTypeId": "%s",
                    "operationalBaseId": "%s"
                }
                """.formatted(TYPE_B738_ID, BASE_EPWA_ID);

        mockMvc.perform(post("/api/aircraft")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void addAircraft_shouldReturn403_whenNotAdmin() throws Exception {
        String token = getToken("schedule_planner", "test1234");
        String body = """
                {
                    "registrationNumber": "SP-NOPERM",
                    "aircraftTypeId": "%s",
                    "operationalBaseId": "%s"
                }
                """.formatted(TYPE_B738_ID, BASE_EPWA_ID);

        mockMvc.perform(post("/api/aircraft")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAircraft_shouldReturnFleetList() throws Exception {
        String token = getToken("admin", "test1234");

        // Add an aircraft first
        String body = """
                {
                    "registrationNumber": "SP-GET",
                    "aircraftTypeId": "%s",
                    "operationalBaseId": "%s"
                }
                """.formatted(TYPE_B738_ID, BASE_EPWA_ID);

        mockMvc.perform(post("/api/aircraft")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        // Get fleet list
        mockMvc.perform(get("/api/aircraft")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[?(@.registrationNumber == 'SP-GET')]").exists());
    }

    private String getToken(String username, String password) throws Exception {
        String tokenUrl = keycloak.getAuthServerUrl() + "/realms/skyroster/protocol/openid-connect/token";

        String reqBody = "grant_type=password"
                + "&client_id=skyroster-frontend"
                + "&username=" + username
                + "&password=" + password;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(tokenUrl))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(reqBody))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to get token: " + response.body());
        }

        JsonNode json = objectMapper.readTree(response.body());
        return json.get("access_token").asText();
    }
}
