package pl.skyroster.skyroster_backend.infrastructure.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.skyroster.skyroster_backend.domain.exception.*;
import pl.skyroster.skyroster_backend.generated.model.ErrorResponse;
import pl.skyroster.skyroster_backend.generated.model.RuleViolationErrorResponse;

import java.time.OffsetDateTime;
import java.util.List;

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

    @ExceptionHandler(FlightTimeConflictException.class)
    public ResponseEntity<ErrorResponse> handleFlightTimeConflict(FlightTimeConflictException ex,
                                                                   HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(RuleViolationException.class)
    public ResponseEntity<RuleViolationErrorResponse> handleRuleViolation(RuleViolationException ex,
                                                                            HttpServletRequest request) {
        List<pl.skyroster.skyroster_backend.generated.model.RuleViolation> apiViolations = ex.getViolations().stream()
                .map(v -> new pl.skyroster.skyroster_backend.generated.model.RuleViolation(
                        v.ruleId(),
                        v.ruleName(),
                        pl.skyroster.skyroster_backend.generated.model.RuleType.valueOf(v.ruleType().name()),
                        v.message()
                ))
                .toList();
        RuleViolationErrorResponse body = new RuleViolationErrorResponse(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                HttpStatus.UNPROCESSABLE_ENTITY.getReasonPhrase(),
                "Cannot create flight: rule violations detected",
                OffsetDateTime.now(),
                request.getRequestURI(),
                apiViolations
        );
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(body);
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

    @ExceptionHandler(PilotAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handlePilotAlreadyExists(PilotAlreadyExistsException ex,HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(OperationalBaseNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOperationalBaseNotFound(OperationalBaseNotFoundException ex,
                                                                        HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
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
