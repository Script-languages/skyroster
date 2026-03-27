package pl.skyroster.skyroster_backend.domain.exception;

import java.util.UUID;

public class AircraftTypeNotFoundException extends RuntimeException {
    public AircraftTypeNotFoundException(UUID id) {
        super("Aircraft type with id '%s' not found".formatted(id));
    }
}
