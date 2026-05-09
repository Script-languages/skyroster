package pl.skyroster.skyroster_backend.domain.service;

import org.springframework.stereotype.Service;
import pl.skyroster.skyroster_backend.domain.model.Flight;
import pl.skyroster.skyroster_backend.domain.repository.FlightRepository;

import java.util.List;

@Service
public class FlightService {

    private final FlightRepository flightRepository;

    public FlightService(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    public List<Flight> getAllFlights() {
        return flightRepository.findAll();
    }
}