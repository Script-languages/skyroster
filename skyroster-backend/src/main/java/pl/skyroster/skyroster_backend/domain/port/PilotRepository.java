package pl.skyroster.skyroster_backend.domain.port;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import pl.skyroster.skyroster_backend.domain.model.Pilot;

import java.util.Optional;
import java.util.UUID;

public interface PilotRepository {
  @EntityGraph(attributePaths = {
      "qualifications",
      "aircraftTypes",
      "operationalBase"
  })
  @Query("SELECT p FROM Pilot p")
  Page<Pilot> findAllWithRelations(Pageable pageable);
  Optional<Pilot> findById(UUID id);
  boolean existsById(UUID id);
  void deleteById(UUID id);
  Pilot save(Pilot pilot);
}