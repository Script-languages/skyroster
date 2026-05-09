package pl.skyroster.skyroster_backend.domain.port;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import pl.skyroster.skyroster_backend.domain.model.Pilot;

public interface PilotRepository {
  @EntityGraph(attributePaths = {
      "qualifications",
      "aircraftTypes",
      "operationalBase"
  })
  @Query("SELECT p FROM Pilot p")
  Page<Pilot> findAllWithRelations(Pageable pageable);
}