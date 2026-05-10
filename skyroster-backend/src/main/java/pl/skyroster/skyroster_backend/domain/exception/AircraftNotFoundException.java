package pl.skyroster.skyroster_backend.domain.exception;

public class AircraftNotFoundException extends RuntimeException {
    public AircraftNotFoundException() {
        super("Aircraft not found");
    }
}
