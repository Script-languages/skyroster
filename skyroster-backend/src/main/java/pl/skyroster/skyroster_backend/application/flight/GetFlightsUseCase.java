package pl.skyroster.skyroster_backend.application.flight;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.skyroster.skyroster_backend.domain.port.FlightRepository;
import pl.skyroster.skyroster_backend.generated.model.FlightResponse;
import pl.skyroster.skyroster_backend.infrastructure.mappers.AircraftResponseMapper;
import pl.skyroster.skyroster_backend.infrastructure.mappers.OperationalBaseInfoMapper;

import java.util.List;

@Service
public class GetFlightsUseCase {
    private final FlightRepository flightRepository;

    public GetFlightsUseCase(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    @Transactional(readOnly = true)
    public List<FlightResponse> execute() {
        return flightRepository.findAll().stream().map(flight -> {
            var aircraftResponse = AircraftResponseMapper.map(flight.getAircraft());

            return new FlightResponse(
                    flight.getId(),
                    aircraftResponse,
                    flight.getFlightStart(),
                    flight.getFlightEnd(),
                    OperationalBaseInfoMapper.map(flight.getStartAirport()),
                    OperationalBaseInfoMapper.map(flight.getEndAirport()),
                    flight.getDescription()
            );
        }).toList();
    }
}
