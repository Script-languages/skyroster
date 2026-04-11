package pl.skyroster.skyroster_backend.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.skyroster.skyroster_backend.domain.model.AircraftType;
import pl.skyroster.skyroster_backend.domain.port.AircraftTypeRepository;

import java.util.UUID;

public interface JpaAircraftTypeRepository extends JpaRepository<AircraftType, UUID>, AircraftTypeRepository {
}
