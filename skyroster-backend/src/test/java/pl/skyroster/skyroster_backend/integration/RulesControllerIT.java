package pl.skyroster.skyroster_backend.integration;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

// Integration test requires Testcontainers and Docker. Disabled by default in CI-less local run.
@Disabled("Requires Docker/Testcontainers - enable when environment supports it")
public class RulesControllerIT {
    @Test
    void placeholder() {
        // Implement integration test using Testcontainers, Postgres and Keycloak if needed.
    }
}
