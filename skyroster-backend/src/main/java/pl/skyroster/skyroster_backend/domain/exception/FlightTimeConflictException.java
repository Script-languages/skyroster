package pl.skyroster.skyroster_backend.domain.exception;

public class FlightTimeConflictException extends RuntimeException {
    public FlightTimeConflictException(String message) {
        super(message);
    }
}
