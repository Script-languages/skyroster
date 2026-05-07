package pl.skyroster.skyroster_backend.domain.model;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
public class Flight {

    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "aircraft_id", nullable = false)
    private Aircraft aircraft;

    @ManyToOne
    @JoinColumn(name = "pilot_id", nullable = false)
    private Pilot pilot;

    @Column(name = "flight_start", nullable = false)
    private OffsetDateTime flightStart;

    @Column(name = "flight_end", nullable = false)
    private OffsetDateTime flightEnd;

    @ManyToOne
    @JoinColumn(name = "start_airport_id")
    private OperationalBase startAirport;

    @ManyToOne
    @JoinColumn(name = "end_airport_id")
    private OperationalBase endAirport;

    private String description;

    public UUID getId() {
        return id;
    }

    public Aircraft getAircraft() {
        return aircraft;
    }

    public Pilot getPilot() {
        return pilot;
    }

    public OffsetDateTime getFlightStart() {
        return flightStart;
    }

    public OffsetDateTime getFlightEnd() {
        return flightEnd;
    }

    public OperationalBase getStartAirport() {
        return startAirport;
    }

    public OperationalBase getEndAirport() {
        return endAirport;
    }

    public String getDescription() {
        return description;
    }
}
