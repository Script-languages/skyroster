package pl.skyroster.skyroster_backend.domain.port;

import pl.skyroster.skyroster_backend.domain.model.Aircraft;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AircraftRepository {
    Aircraft save(Aircraft aircraft);
    List<Aircraft> findAll();
    Optional<Aircraft> findById(UUID id);
    boolean existsByRegistrationNumber(String registrationNumber);
    boolean existsByRegistrationNumberAndIdNot(String registrationNumber, UUID id);
    void deleteById(UUID id);
    List<Aircraft> findAvailable(OffsetDateTime from, OffsetDateTime to, UUID baseIdOrNull);
}
