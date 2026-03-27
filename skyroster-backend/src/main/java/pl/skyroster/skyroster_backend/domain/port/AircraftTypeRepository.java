package pl.skyroster.skyroster_backend.domain.port;

import pl.skyroster.skyroster_backend.domain.model.AircraftType;

import java.util.Optional;
import java.util.UUID;

public interface AircraftTypeRepository {
    Optional<AircraftType> findById(UUID id);
}
