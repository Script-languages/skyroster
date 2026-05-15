package pl.skyroster.skyroster_backend.domain.port;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.skyroster.skyroster_backend.domain.model.Flight;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FlightRepository extends JpaRepository<Flight, UUID> {

    List<Flight> findAll();

    boolean existsByAircraftId(UUID aircraftId);

    boolean existsByPilotIdAndFlightEndAfter(UUID pilotId, OffsetDateTime now);

    @Query("""
        SELECT f FROM Flight f
        WHERE f.aircraft.id = :aircraftId
          AND f.flightStart < :to
          AND f.flightEnd > :from
        """)
    List<Flight> findOverlappingByAircraft(@Param("aircraftId") UUID aircraftId,
                                            @Param("from") OffsetDateTime from,
                                            @Param("to") OffsetDateTime to);

    @Query("""
        SELECT f FROM Flight f
        WHERE f.pilot.id = :pilotId
          AND f.flightStart < :to
          AND f.flightEnd > :from
        """)
    List<Flight> findOverlappingByPilot(@Param("pilotId") UUID pilotId,
                                         @Param("from") OffsetDateTime from,
                                         @Param("to") OffsetDateTime to);

    @Query("""
        SELECT f FROM Flight f
        WHERE f.pilot.id = :pilotId
          AND f.flightStart < :to
          AND f.flightEnd > :from
        ORDER BY f.flightStart
        """)
    List<Flight> findByPilotInPeriod(@Param("pilotId") UUID pilotId,
                                      @Param("from") OffsetDateTime from,
                                      @Param("to") OffsetDateTime to);

    @Query("""
        SELECT f FROM Flight f
        WHERE f.pilot.id = :pilotId
          AND f.flightEnd <= :before
        ORDER BY f.flightEnd DESC
        LIMIT 1
        """)
    Optional<Flight> findLastByPilotBefore(@Param("pilotId") UUID pilotId,
                                            @Param("before") OffsetDateTime before);

    @Query("""
        SELECT f FROM Flight f
        WHERE f.pilot.id = :pilotId
          AND f.flightStart >= :after
        ORDER BY f.flightStart
        LIMIT 1
        """)
    Optional<Flight> findFirstByPilotAfter(@Param("pilotId") UUID pilotId,
                                            @Param("after") OffsetDateTime after);
}
