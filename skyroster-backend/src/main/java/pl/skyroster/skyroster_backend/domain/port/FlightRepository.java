package pl.skyroster.skyroster_backend.domain.port;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.skyroster.skyroster_backend.domain.model.Flight;
import pl.skyroster.skyroster_backend.generated.model.FlightResponse;

import java.util.List;
import java.util.UUID;

public interface FlightRepository extends JpaRepository<Flight, UUID> {
    List<Flight> findAll();
    boolean existsByAircraftId(UUID aircraftId);
}