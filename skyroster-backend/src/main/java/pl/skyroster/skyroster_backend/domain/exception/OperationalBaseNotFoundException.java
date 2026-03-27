package pl.skyroster.skyroster_backend.domain.exception;

import java.util.UUID;

public class OperationalBaseNotFoundException extends RuntimeException {
    public OperationalBaseNotFoundException(UUID id) {
        super("Operational base with id '%s' not found".formatted(id));
    }
}
