package pl.skyroster.skyroster_backend.infrastructure.adapter.in.web;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.skyroster.skyroster_backend.application.pilot.DeletePilotUseCase;
import pl.skyroster.skyroster_backend.application.pilot.GetPilotUseCase;
import pl.skyroster.skyroster_backend.generated.api.ApiApi;
import pl.skyroster.skyroster_backend.generated.model.PagedPilotResponse;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
public class PilotController {

  private final GetPilotUseCase getPilotUseCase;
  private final DeletePilotUseCase deletePilotUseCase;

  @GetMapping(ApiApi.PATH_GET_PILOTS)
  public ResponseEntity<PagedPilotResponse> getPilots(@RequestParam Integer page, @RequestParam Integer size, @RequestParam @Nullable String sort) {
    return ResponseEntity.ok(getPilotUseCase.getPilots(page, size, sort));
  }

  @DeleteMapping(ApiApi.PATH_DELETE_PILOT_BY_ID)
  public ResponseEntity<Void> deletePilotById(@PathVariable UUID pilotId) {
    deletePilotUseCase.deletePilotById(pilotId);
    return ResponseEntity.noContent().build();
  }
}