package pl.skyroster.skyroster_backend.infrastructure.adapter.in.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.skyroster.skyroster_backend.application.flight.CreateFlightUseCase;
import pl.skyroster.skyroster_backend.application.flight.GetFlightsUseCase;
import pl.skyroster.skyroster_backend.domain.model.Flight;
import pl.skyroster.skyroster_backend.generated.api.ApiApi;
import pl.skyroster.skyroster_backend.generated.model.CreateFlightRequest;
import pl.skyroster.skyroster_backend.generated.model.FlightResponse;
import pl.skyroster.skyroster_backend.infrastructure.mappers.FlightResponseMapper;

import java.util.List;

@RestController
public class FlightController {

    private final GetFlightsUseCase getFlightsUseCase;
    private final CreateFlightUseCase createFlightUseCase;

    public FlightController(GetFlightsUseCase getFlightsUseCase, CreateFlightUseCase createFlightUseCase) {
        this.getFlightsUseCase = getFlightsUseCase;
        this.createFlightUseCase = createFlightUseCase;
    }

    @GetMapping(ApiApi.PATH_DISPLAY_FLIGHTS)
    public List<FlightResponse> getFlights() {
        return getFlightsUseCase.execute();
    }

    @PostMapping(ApiApi.PATH_CREATE_FLIGHT)
    public ResponseEntity<FlightResponse> createFlight(@RequestBody CreateFlightRequest request) {
        Flight flight = createFlightUseCase.execute(
                request.getAircraftId(),
                request.getPilotId(),
                request.getStartDateTime(),
                request.getEndDateTime(),
                request.getStartAirportId(),
                request.getEndAirportId(),
                request.getDescription()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(FlightResponseMapper.map(flight));
    }
}
