package pl.skyroster.skyroster_backend.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.skyroster.skyroster_backend.domain.model.Aircraft;
import pl.skyroster.skyroster_backend.domain.port.AircraftRepository;

import java.util.UUID;

public interface JpaAircraftRepository extends JpaRepository<Aircraft, UUID>, AircraftRepository {
}
