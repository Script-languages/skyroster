package pl.skyroster.skyroster_backend.infrastructure.adapter.in.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.skyroster.skyroster_backend.application.aircraft.AddAircraftUseCase;
import pl.skyroster.skyroster_backend.application.aircraft.DeleteAircraftUseCase;
import pl.skyroster.skyroster_backend.application.aircraft.GetAircraftUseCase;
import pl.skyroster.skyroster_backend.application.aircraft.UpdateAircraftUseCase;
import pl.skyroster.skyroster_backend.domain.model.Aircraft;
import pl.skyroster.skyroster_backend.generated.api.ApiApi;
import pl.skyroster.skyroster_backend.generated.model.AddAircraftRequest;
import pl.skyroster.skyroster_backend.generated.model.AircraftResponse;
import pl.skyroster.skyroster_backend.generated.model.AircraftTypeInfo;
import pl.skyroster.skyroster_backend.generated.model.OperationalBaseInfo;
import pl.skyroster.skyroster_backend.generated.model.UpdateAircraftRequest;

import java.util.List;
import java.util.UUID;

@RestController
public class AircraftController {

    private final AddAircraftUseCase addAircraftUseCase;
    private final GetAircraftUseCase getAircraftUseCase;
    private final UpdateAircraftUseCase updateAircraftUseCase;
    private final DeleteAircraftUseCase deleteAircraftUseCase;

    public AircraftController(AddAircraftUseCase addAircraftUseCase,
                              GetAircraftUseCase getAircraftUseCase,
                              UpdateAircraftUseCase updateAircraftUseCase,
                              DeleteAircraftUseCase deleteAircraftUseCase) {
        this.addAircraftUseCase = addAircraftUseCase;
        this.getAircraftUseCase = getAircraftUseCase;
        this.updateAircraftUseCase = updateAircraftUseCase;
        this.deleteAircraftUseCase = deleteAircraftUseCase;
    }

    @PostMapping(ApiApi.PATH_ADD_AIRCRAFT)
    public ResponseEntity<AircraftResponse> addAircraft(@RequestBody AddAircraftRequest request) {
        Aircraft aircraft = addAircraftUseCase.execute(
                request.getRegistrationNumber(),
                request.getAircraftTypeCode(),
                request.getOperationalBaseCode()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(aircraft));
    }

    @PutMapping(ApiApi.PATH_UPDATE_AIRCRAFT)
    public ResponseEntity<AircraftResponse> updateAircraft(@PathVariable("id") UUID id,
                                                            @RequestBody UpdateAircraftRequest request) {
        Aircraft aircraft = updateAircraftUseCase.execute(
                id,
                request.getRegistrationNumber(),
                request.getAircraftTypeCode(),
                request.getOperationalBaseCode()
        );
        return ResponseEntity.ok(toResponse(aircraft));
    }

    @DeleteMapping(ApiApi.PATH_DELETE_AIRCRAFT)
    public ResponseEntity<Void> deleteAircraft(@PathVariable("id") UUID id) {
        deleteAircraftUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(ApiApi.PATH_GET_AIRCRAFT)
    public ResponseEntity<List<AircraftResponse>> getAircraft() {
        List<AircraftResponse> response = getAircraftUseCase.execute().stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    private AircraftResponse toResponse(Aircraft aircraft) {
        return new AircraftResponse()
                .id(aircraft.getId())
                .registrationNumber(aircraft.getRegistrationNumber())
                .aircraftType(new AircraftTypeInfo()
                        .id(aircraft.getAircraftType().getId())
                        .icaoCode(aircraft.getAircraftType().getIcaoCode())
                        .name(aircraft.getAircraftType().getName()))
                .operationalBase(new OperationalBaseInfo()
                        .id(aircraft.getOperationalBase().getId())
                        .icaoCode(aircraft.getOperationalBase().getIcaoCode())
                        .name(aircraft.getOperationalBase().getName()));
    }
}
