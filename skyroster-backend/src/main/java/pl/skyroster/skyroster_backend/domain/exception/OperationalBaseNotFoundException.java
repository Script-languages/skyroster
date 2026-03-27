package pl.skyroster.skyroster_backend.domain.exception;

public class OperationalBaseNotFoundException extends RuntimeException {
    public OperationalBaseNotFoundException(String icaoCode) {
        super("Operational base with code '%s' not found".formatted(icaoCode));
    }
}
