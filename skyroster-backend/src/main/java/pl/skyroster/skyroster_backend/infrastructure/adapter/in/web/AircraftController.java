package pl.skyroster.skyroster_backend.infrastructure.adapter.in.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.skyroster.skyroster_backend.application.aircraft.AddAircraftUseCase;
import pl.skyroster.skyroster_backend.application.aircraft.GetAircraftUseCase;
import pl.skyroster.skyroster_backend.domain.model.Aircraft;
import pl.skyroster.skyroster_backend.generated.api.ApiApi;
import pl.skyroster.skyroster_backend.generated.model.AddAircraftRequest;
import pl.skyroster.skyroster_backend.generated.model.AircraftResponse;
import pl.skyroster.skyroster_backend.generated.model.AircraftTypeInfo;
import pl.skyroster.skyroster_backend.generated.model.OperationalBaseInfo;

import java.util.List;

@RestController
public class AircraftController {

    private final AddAircraftUseCase addAircraftUseCase;
    private final GetAircraftUseCase getAircraftUseCase;

    public AircraftController(AddAircraftUseCase addAircraftUseCase, GetAircraftUseCase getAircraftUseCase) {
        this.addAircraftUseCase = addAircraftUseCase;
        this.getAircraftUseCase = getAircraftUseCase;
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
