package pl.skyroster.skyroster_backend.infrastructure.adapter.in.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.skyroster.skyroster_backend.application.flight.GetFlightsUseCase;
import pl.skyroster.skyroster_backend.domain.model.Flight;
import pl.skyroster.skyroster_backend.generated.model.FlightResponse;

import java.util.List;

@RestController
@RequestMapping("/api/flights")
public class FlightController {
    private final GetFlightsUseCase getFlightsUseCase;

    public FlightController(GetFlightsUseCase getFlightsUseCase) {
        this.getFlightsUseCase = getFlightsUseCase;
    }

    @GetMapping
    public List<FlightResponse> getFlights() {
        return getFlightsUseCase.execute();
    }
}
