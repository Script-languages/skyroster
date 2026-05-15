package pl.skyroster.skyroster_backend.domain.port;

import pl.skyroster.skyroster_backend.domain.model.OperationalBase;

import java.util.Optional;
import java.util.UUID;

public interface OperationalBaseRepository {
    Optional<OperationalBase> findByIcaoCode(String icaoCode);
    Optional<OperationalBase> findById(UUID id);
}
