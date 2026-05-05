package pl.skyroster.skyroster_backend.domain.port;

import pl.skyroster.skyroster_backend.domain.model.AircraftType;

import java.util.Optional;

public interface AircraftTypeRepository {
    Optional<AircraftType> findByIcaoCode(String icaoCode);
}
