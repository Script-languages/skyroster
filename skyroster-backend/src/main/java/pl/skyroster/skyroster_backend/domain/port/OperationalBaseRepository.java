package pl.skyroster.skyroster_backend.domain.port;

import pl.skyroster.skyroster_backend.domain.model.OperationalBase;

import java.util.Optional;

public interface OperationalBaseRepository {
    Optional<OperationalBase> findByIcaoCode(String icaoCode);
}
