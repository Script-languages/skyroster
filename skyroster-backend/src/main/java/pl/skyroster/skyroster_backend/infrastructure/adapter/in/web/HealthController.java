package pl.skyroster.skyroster_backend.infrastructure.adapter.in.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.skyroster.skyroster_backend.generated.api.ApiApi;
import pl.skyroster.skyroster_backend.generated.model.HealthStatus;

@RestController
public class HealthController {

    @GetMapping(ApiApi.PATH_GET_HEALTH)
    public ResponseEntity<HealthStatus> getHealth() {
        return ResponseEntity.ok(new HealthStatus().status("UP"));
    }
}