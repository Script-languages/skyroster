package pl.skyroster.skyroster_backend.infrastructure.adapter.in.web;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.skyroster.skyroster_backend.application.pilot.DeletePilotUseCase;
import pl.skyroster.skyroster_backend.application.pilot.GetPilotUseCase;
import pl.skyroster.skyroster_backend.application.pilot.PatchPilotUseCase;
import pl.skyroster.skyroster_backend.generated.api.ApiApi;
import pl.skyroster.skyroster_backend.generated.model.PagedPilotResponse;
import pl.skyroster.skyroster_backend.generated.model.PilotPatchRequest;
import pl.skyroster.skyroster_backend.generated.model.PilotResponse;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
public class PilotController {

  private final GetPilotUseCase getPilotUseCase;
  private final DeletePilotUseCase deletePilotUseCase;
  private final PatchPilotUseCase patchPilotUseCase;

  @GetMapping(ApiApi.PATH_GET_PILOTS)
  public ResponseEntity<PagedPilotResponse> getPilots(@RequestParam Integer page, @RequestParam Integer size, @RequestParam @Nullable String sort) {
    return ResponseEntity.ok(getPilotUseCase.getPilots(page, size, sort));
  }

  @DeleteMapping(ApiApi.PATH_DELETE_PILOT_BY_ID)
  public ResponseEntity<Void> deletePilotById(@PathVariable UUID pilotId) {
    deletePilotUseCase.deletePilotById(pilotId);
    return ResponseEntity.noContent().build();
  }

  @PatchMapping(ApiApi.PATH_PATCH_PILOT)
  public ResponseEntity<PilotResponse> patchPilot(@PathVariable UUID pilotId, @RequestBody PilotPatchRequest request) {
    return ResponseEntity.ok(patchPilotUseCase.patchPilot(pilotId, request));
  }
}