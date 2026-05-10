package pl.skyroster.skyroster_backend.infrastructure.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.skyroster.skyroster_backend.domain.exception.AircraftAlreadyExistsException;
import pl.skyroster.skyroster_backend.domain.exception.AircraftHasAssignedFlightsException;
import pl.skyroster.skyroster_backend.domain.exception.AircraftNotFoundException;
import pl.skyroster.skyroster_backend.domain.exception.AircraftTypeNotFoundException;
import pl.skyroster.skyroster_backend.domain.exception.OperationalBaseNotFoundException;
import pl.skyroster.skyroster_backend.domain.exception.PilotNotFoundException;
import pl.skyroster.skyroster_backend.generated.model.ErrorResponse;

import java.time.OffsetDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AircraftAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleAircraftAlreadyExists(AircraftAlreadyExistsException ex,
                                                                      HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(AircraftNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAircraftNotFound(AircraftNotFoundException ex,
                                                                 HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(AircraftHasAssignedFlightsException.class)
    public ResponseEntity<ErrorResponse> handleAircraftHasAssignedFlights(AircraftHasAssignedFlightsException ex,
                                                                           HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex,
                                                                        HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT,
                "Aircraft cannot be deleted because it has assigned flights",
                request);
    }

    @ExceptionHandler(AircraftTypeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAircraftTypeNotFound(AircraftTypeNotFoundException ex,
                                                                     HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(PilotNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePilotNotFound(PilotNotFoundException ex,HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(OperationalBaseNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOperationalBaseNotFound(OperationalBaseNotFoundException ex,
                                                                        HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex,
                                                                HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(Exception ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message,
                                                         HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse()
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .timestamp(OffsetDateTime.now())
                .path(request.getRequestURI());
        return ResponseEntity.status(status).body(error);
    }
}
