package pl.skyroster.skyroster_backend.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AircraftValidationTest {

    @Test
    void shouldThrowOnNullRegistrationNumber() {
        assertThatThrownBy(() -> Aircraft.validateRegistrationNumber(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Registration number must not be blank");
    }

    @Test
    void shouldThrowOnBlankRegistrationNumber() {
        assertThatThrownBy(() -> Aircraft.validateRegistrationNumber("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Registration number must not be blank");
    }

    @Test
    void shouldAcceptValidRegistrationNumber() {
        assertThatCode(() -> Aircraft.validateRegistrationNumber("SP-LRA"))
                .doesNotThrowAnyException();
    }
}
