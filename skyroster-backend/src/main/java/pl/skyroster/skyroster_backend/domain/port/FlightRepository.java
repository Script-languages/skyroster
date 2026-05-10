package pl.skyroster.skyroster_backend.domain.port;

import pl.skyroster.skyroster_backend.domain.model.Flight;

import java.util.List;
import java.util.UUID;

public interface FlightRepository {
    List<Flight> findAll();
    boolean existsByAircraftId(UUID aircraftId);
}