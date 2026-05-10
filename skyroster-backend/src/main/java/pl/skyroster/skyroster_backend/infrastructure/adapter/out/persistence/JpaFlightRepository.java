package pl.skyroster.skyroster_backend.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.skyroster.skyroster_backend.domain.model.Flight;
import pl.skyroster.skyroster_backend.domain.port.FlightRepository;

import java.util.UUID;

public interface JpaFlightRepository extends JpaRepository<Flight, UUID>, FlightRepository {
}
