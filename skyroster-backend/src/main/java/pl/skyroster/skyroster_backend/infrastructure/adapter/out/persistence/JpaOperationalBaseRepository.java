package pl.skyroster.skyroster_backend.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.skyroster.skyroster_backend.domain.model.OperationalBase;
import pl.skyroster.skyroster_backend.domain.port.OperationalBaseRepository;

import java.util.UUID;

public interface JpaOperationalBaseRepository extends JpaRepository<OperationalBase, UUID>, OperationalBaseRepository {
}
