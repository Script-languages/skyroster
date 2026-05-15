package pl.skyroster.skyroster_backend.domain.exception;

import java.util.UUID;

public class PilotNotFoundException extends RuntimeException {
  public PilotNotFoundException(UUID pilotId) {
    super("Pilot with id " + pilotId + " not found");
  }
}
