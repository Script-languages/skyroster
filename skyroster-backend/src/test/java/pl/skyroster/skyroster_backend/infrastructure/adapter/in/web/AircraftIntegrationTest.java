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

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

    @Test
    void addAircraft_shouldReturn201_whenAdminWithValidData() throws Exception {
        String token = getToken("admin", "test1234");
        String body = """
                {
                    "registrationNumber": "SP-LRA",
                    "aircraftTypeCode": "B738",
                    "operationalBaseCode": "EPWA"
                }
                """;

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
                    "aircraftTypeCode": "B738",
                    "operationalBaseCode": "EPWA"
                }
                """;

        mockMvc.perform(post("/api/aircraft")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

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
                    "aircraftTypeCode": "XXXX",
                    "operationalBaseCode": "EPWA"
                }
                """;

        mockMvc.perform(post("/api/aircraft")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void addAircraft_shouldReturn404_whenOperationalBaseNotFound() throws Exception {
        String token = getToken("admin", "test1234");
        String body = """
                {
                    "registrationNumber": "SP-BAD2",
                    "aircraftTypeCode": "B738",
                    "operationalBaseCode": "XXXX"
                }
                """;

        mockMvc.perform(post("/api/aircraft")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void addAircraft_shouldReturn401_whenNoToken() throws Exception {
        String body = """
                {
                    "registrationNumber": "SP-NO",
                    "aircraftTypeCode": "B738",
                    "operationalBaseCode": "EPWA"
                }
                """;

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
                    "aircraftTypeCode": "B738",
                    "operationalBaseCode": "EPWA"
                }
                """;

        mockMvc.perform(post("/api/aircraft")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAircraft_shouldReturnFleetList() throws Exception {
        String token = getToken("admin", "test1234");

        String body = """
                {
                    "registrationNumber": "SP-GET",
                    "aircraftTypeCode": "A320",
                    "operationalBaseCode": "EPKK"
                }
                """;

        mockMvc.perform(post("/api/aircraft")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/aircraft")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[?(@.registrationNumber == 'SP-GET')]").exists());
    }

    @Test
    void updateAircraft_shouldReturn200_whenAdminWithValidData() throws Exception {
        String token = getToken("admin", "test1234");
        UUID aircraftId = createAircraft(token, "SP-UPD1", "B738", "EPWA");

        String body = """
                {
                    "registrationNumber": "SP-UPD2",
                    "aircraftTypeCode": "A320",
                    "operationalBaseCode": "EPKK"
                }
                """;

        mockMvc.perform(put("/api/aircraft/" + aircraftId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(aircraftId.toString()))
                .andExpect(jsonPath("$.registrationNumber").value("SP-UPD2"))
                .andExpect(jsonPath("$.aircraftType.icaoCode").value("A320"))
                .andExpect(jsonPath("$.operationalBase.icaoCode").value("EPKK"));
    }

    @Test
    void updateAircraft_shouldReturn404_whenAircraftDoesNotExist() throws Exception {
        String token = getToken("admin", "test1234");
        String body = """
                {
                    "registrationNumber": "SP-X",
                    "aircraftTypeCode": "B738",
                    "operationalBaseCode": "EPWA"
                }
                """;
        mockMvc.perform(put("/api/aircraft/" + UUID.randomUUID())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Aircraft not found"));
    }

    @Test
    void updateAircraft_shouldReturn409_whenRegistrationTakenByAnotherAircraft() throws Exception {
        String token = getToken("admin", "test1234");
        UUID aircraftA = createAircraft(token, "SP-RA1", "B738", "EPWA");
        createAircraft(token, "SP-RA2", "B738", "EPWA");

        String body = """
                {
                    "registrationNumber": "SP-RA2",
                    "aircraftTypeCode": "B738",
                    "operationalBaseCode": "EPWA"
                }
                """;
        mockMvc.perform(put("/api/aircraft/" + aircraftA)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict());
    }

    @Test
    void updateAircraft_shouldAllow_keepingSameRegistrationOnSameAircraft() throws Exception {
        String token = getToken("admin", "test1234");
        UUID aircraftId = createAircraft(token, "SP-IDM", "B738", "EPWA");

        String body = """
                {
                    "registrationNumber": "SP-IDM",
                    "aircraftTypeCode": "A320",
                    "operationalBaseCode": "EPKK"
                }
                """;
        mockMvc.perform(put("/api/aircraft/" + aircraftId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.aircraftType.icaoCode").value("A320"));

        mockMvc.perform(get("/api/aircraft")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == '" + aircraftId + "')].aircraftType.icaoCode").value(org.hamcrest.Matchers.contains("A320")))
                .andExpect(jsonPath("$[?(@.id == '" + aircraftId + "')].operationalBase.icaoCode").value(org.hamcrest.Matchers.contains("EPKK")));
    }

    @Test
    void updateAircraft_shouldReturn400_whenAircraftTypeNotFound() throws Exception {
        String token = getToken("admin", "test1234");
        UUID aircraftId = createAircraft(token, "SP-TYP", "B738", "EPWA");

        String body = """
                {
                    "registrationNumber": "SP-TYP",
                    "aircraftTypeCode": "ZZZZ",
                    "operationalBaseCode": "EPWA"
                }
                """;
        mockMvc.perform(put("/api/aircraft/" + aircraftId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateAircraft_shouldReturn404_whenOperationalBaseNotFound() throws Exception {
        String token = getToken("admin", "test1234");
        UUID aircraftId = createAircraft(token, "SP-BAS", "B738", "EPWA");

        String body = """
                {
                    "registrationNumber": "SP-BAS",
                    "aircraftTypeCode": "B738",
                    "operationalBaseCode": "ZZZZ"
                }
                """;
        mockMvc.perform(put("/api/aircraft/" + aircraftId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateAircraft_shouldReturn400_whenRegistrationIsBlank() throws Exception {
        String token = getToken("admin", "test1234");
        UUID aircraftId = createAircraft(token, "SP-BLK", "B738", "EPWA");

        String body = """
                {
                    "registrationNumber": "   ",
                    "aircraftTypeCode": "B738",
                    "operationalBaseCode": "EPWA"
                }
                """;
        mockMvc.perform(put("/api/aircraft/" + aircraftId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteAircraft_shouldReturn204_whenAircraftHasNoFlights() throws Exception {
        String token = getToken("admin", "test1234");
        UUID aircraftId = createAircraft(token, "SP-DEL1", "B738", "EPWA");

        mockMvc.perform(delete("/api/aircraft/" + aircraftId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/aircraft")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == '" + aircraftId + "')]").isEmpty());
    }

    @Test
    void deleteAircraft_shouldReturn404_whenAircraftDoesNotExist() throws Exception {
        String token = getToken("admin", "test1234");

        mockMvc.perform(delete("/api/aircraft/" + UUID.randomUUID())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Aircraft not found"));
    }

    @Test
    void deleteAircraft_shouldReturn409_whenAircraftHasAssignedFlights() throws Exception {
        String token = getToken("admin", "test1234");
        // Use the seeded aircraft SP-KWA which has assigned flights from V2__aircraft_schema_and_seed.sql
        UUID seededAircraftId = UUID.fromString("e0000000-0000-0000-0000-000000000001");

        mockMvc.perform(delete("/api/aircraft/" + seededAircraftId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(
                        org.hamcrest.Matchers.containsString("SP-KWA")));
    }

    private UUID createAircraft(String token, String registration, String typeCode, String baseCode) throws Exception {
        String body = """
                {
                    "registrationNumber": "%s",
                    "aircraftTypeCode": "%s",
                    "operationalBaseCode": "%s"
                }
                """.formatted(registration, typeCode, baseCode);

        String response = mockMvc.perform(post("/api/aircraft")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        JsonNode json = objectMapper.readTree(response);
        return UUID.fromString(json.get("id").asText());
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
