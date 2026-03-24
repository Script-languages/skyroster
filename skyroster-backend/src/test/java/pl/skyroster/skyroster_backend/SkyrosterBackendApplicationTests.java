package pl.skyroster.skyroster_backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class SkyrosterBackendApplicationTests {

    @Test
    void contextLoads() {
        // verifies that the Spring application context starts without errors
    }
}