package pl.skyroster.skyroster_backend.infrastructure.adapter.in.web;

import com.fasterxml.jackson.databind.JsonNode;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class FlightIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private KeycloakContainer keycloak;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void getFlights_shouldReturnFlightsList() throws Exception {
        String token = getToken("admin", "test1234");
        mockMvc.perform(get("/api/flights")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
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