package pl.skyroster.skyroster_backend.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.skyroster.skyroster_backend.domain.model.Aircraft;
import pl.skyroster.skyroster_backend.domain.port.AircraftRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface JpaAircraftRepository extends JpaRepository<Aircraft, UUID>, AircraftRepository {

    @Override
    @Query("""
        SELECT a FROM Aircraft a
        WHERE (:baseId IS NULL OR a.operationalBase.id = :baseId)
          AND a.id NOT IN (
              SELECT f.aircraft.id FROM Flight f
              WHERE f.flightStart < :to AND f.flightEnd > :from
          )
        """)
    List<Aircraft> findAvailable(@Param("from") OffsetDateTime from,
                                  @Param("to") OffsetDateTime to,
                                  @Param("baseId") UUID baseIdOrNull);
}
