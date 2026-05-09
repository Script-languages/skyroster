package pl.skyroster.skyroster_backend.application.flight;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.skyroster.skyroster_backend.domain.model.Flight;
import pl.skyroster.skyroster_backend.domain.port.FlightRepository;

import java.util.List;

@Service
public class GetFlightsUseCase {
    private final FlightRepository flightRepository;

    public GetFlightsUseCase(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    @Transactional(readOnly = true)
    public List<Flight> execute() {
        return flightRepository.findAll();
    }
}
