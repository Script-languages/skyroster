package pl.skyroster.skyroster_backend.domain.exception;

public class AircraftHasAssignedFlightsException extends RuntimeException {
    public AircraftHasAssignedFlightsException(String registrationNumber) {
        super("Aircraft " + registrationNumber + " cannot be deleted because it has assigned flights");
    }
}
