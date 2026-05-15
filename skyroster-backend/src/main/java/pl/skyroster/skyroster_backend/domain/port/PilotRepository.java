package pl.skyroster.skyroster_backend.domain.port;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.skyroster.skyroster_backend.domain.model.Pilot;

import java.time.OffsetDateTime;
import java.util.List;
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

  boolean existsByLicence(String licence);

  void deleteById(UUID id);

  Pilot save(Pilot pilot);

  @EntityGraph(attributePaths = {"qualifications", "aircraftTypes", "operationalBase"})
  @Query("""
      SELECT DISTINCT p FROM Pilot p
      LEFT JOIN p.aircraftTypes at
      WHERE (:aircraftTypeId IS NULL OR at.id = :aircraftTypeId)
        AND p.id NOT IN (
            SELECT f.pilot.id FROM Flight f
            WHERE f.flightStart < :to AND f.flightEnd > :from
        )
      """)
  List<Pilot> findAvailable(@Param("from") OffsetDateTime from,
                            @Param("to") OffsetDateTime to,
                            @Param("aircraftTypeId") UUID aircraftTypeIdOrNull);
}
