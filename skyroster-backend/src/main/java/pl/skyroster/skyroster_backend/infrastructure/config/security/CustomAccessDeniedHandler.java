package pl.skyroster.skyroster_backend.infrastructure.config.security;

import tools.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import pl.skyroster.skyroster_backend.generated.model.ErrorResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.OffsetDateTime;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private static final Logger log = LoggerFactory.getLogger(CustomAccessDeniedHandler.class);
    private static final String GENERIC_MESSAGE = "You do not have permission to access this resource";

    private final ObjectMapper objectMapper;

    public CustomAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        log.warn("Access denied for {}: {}", request.getRequestURI(), accessDeniedException.getMessage());

        HttpStatus status = HttpStatus.FORBIDDEN;

        ErrorResponse error = new ErrorResponse()
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(GENERIC_MESSAGE)
                .timestamp(OffsetDateTime.now())
                .path(request.getRequestURI());

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), error);
    }
}