package pl.skyroster.skyroster_backend.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.skyroster.skyroster_backend.domain.model.Pilot;
import pl.skyroster.skyroster_backend.domain.port.PilotRepository;

import java.util.UUID;

public interface JpaPilotRepository extends JpaRepository<Pilot, UUID>, PilotRepository {
}
