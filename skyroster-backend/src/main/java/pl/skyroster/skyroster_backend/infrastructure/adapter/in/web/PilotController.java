package pl.skyroster.skyroster_backend.infrastructure.adapter.in.web;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.skyroster.skyroster_backend.application.pilot.GetPilotUseCase;
import pl.skyroster.skyroster_backend.generated.api.ApiApi;
import pl.skyroster.skyroster_backend.generated.model.PagedPilotResponse;

@RequiredArgsConstructor
@RestController
public class PilotController {

  private final GetPilotUseCase getPilotUseCase;

  @GetMapping(ApiApi.PATH_GET_PILOTS)
  public ResponseEntity<PagedPilotResponse> getPilots(@RequestParam Integer page, @RequestParam Integer size, @RequestParam @Nullable String sort) {
    return ResponseEntity.ok(getPilotUseCase.getPilots(page, size, sort));
  }
}