package pl.skyroster.skyroster_backend.application.flight;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.skyroster.skyroster_backend.domain.port.FlightRepository;
import pl.skyroster.skyroster_backend.generated.model.FlightResponse;
import pl.skyroster.skyroster_backend.infrastructure.mappers.FlightResponseMapper;

import java.util.List;

@Service
public class GetFlightsUseCase {
    private final FlightRepository flightRepository;

    public GetFlightsUseCase(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    @Transactional(readOnly = true)
    public List<FlightResponse> execute() {
        return flightRepository.findAll().stream()
                .map(FlightResponseMapper::map)
                .toList();
    }
}
