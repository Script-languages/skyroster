package pl.skyroster.skyroster_backend.domain.exception;

public class AircraftAlreadyExistsException extends RuntimeException {
    public AircraftAlreadyExistsException(String registrationNumber) {
        super("Aircraft with registration number '%s' already exists".formatted(registrationNumber));
    }
}
