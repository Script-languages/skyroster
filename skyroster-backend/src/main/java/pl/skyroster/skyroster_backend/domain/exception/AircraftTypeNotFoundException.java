package pl.skyroster.skyroster_backend.domain.exception;

public class AircraftTypeNotFoundException extends RuntimeException {
    public AircraftTypeNotFoundException(String icaoCode) {
        super("Aircraft type with code '%s' not found".formatted(icaoCode));
    }
}
