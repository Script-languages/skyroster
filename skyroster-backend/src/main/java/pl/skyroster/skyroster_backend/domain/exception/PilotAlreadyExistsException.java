package pl.skyroster.skyroster_backend.domain.exception;

public class PilotAlreadyExistsException extends RuntimeException {
  public PilotAlreadyExistsException(String message) {
    super(message);
  }
}
