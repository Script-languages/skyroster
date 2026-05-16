package pl.skyroster.skyroster_backend.application.flight;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.skyroster.skyroster_backend.domain.exception.AircraftNotFoundException;
import pl.skyroster.skyroster_backend.domain.exception.FlightTimeConflictException;
import pl.skyroster.skyroster_backend.domain.exception.OperationalBaseNotFoundException;
import pl.skyroster.skyroster_backend.domain.exception.PilotNotFoundException;
import pl.skyroster.skyroster_backend.domain.exception.RuleViolationException;
import pl.skyroster.skyroster_backend.domain.model.Aircraft;
import pl.skyroster.skyroster_backend.domain.model.Flight;
import pl.skyroster.skyroster_backend.domain.model.OperationalBase;
import pl.skyroster.skyroster_backend.domain.model.Pilot;
import pl.skyroster.skyroster_backend.domain.model.Rule;
import pl.skyroster.skyroster_backend.domain.model.RuleViolation;
import pl.skyroster.skyroster_backend.domain.port.AircraftRepository;
import pl.skyroster.skyroster_backend.domain.port.FlightRepository;
import pl.skyroster.skyroster_backend.domain.port.OperationalBaseRepository;
import pl.skyroster.skyroster_backend.domain.port.PilotRepository;
import pl.skyroster.skyroster_backend.domain.port.RuleRepository;
import pl.skyroster.skyroster_backend.domain.service.RuleValidator;

import java.time.OffsetDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.UUID;

@Service
public class CreateFlightUseCase {

    private final AircraftRepository aircraftRepository;
    private final PilotRepository pilotRepository;
    private final OperationalBaseRepository operationalBaseRepository;
    private final FlightRepository flightRepository;
    private final RuleRepository ruleRepository;
    private final RuleValidator ruleValidator = new RuleValidator();

    public CreateFlightUseCase(AircraftRepository aircraftRepository,
                                PilotRepository pilotRepository,
                                OperationalBaseRepository operationalBaseRepository,
                                FlightRepository flightRepository,
                                RuleRepository ruleRepository) {
        this.aircraftRepository = aircraftRepository;
        this.pilotRepository = pilotRepository;
        this.operationalBaseRepository = operationalBaseRepository;
        this.flightRepository = flightRepository;
        this.ruleRepository = ruleRepository;
    }

    @Transactional
    public Flight execute(UUID aircraftId, UUID pilotId,
                           OffsetDateTime startDateTime, OffsetDateTime endDateTime,
                           UUID startAirportId, UUID endAirportIdOrNull,
                           String descriptionOrNull) {
        if (startDateTime == null || endDateTime == null || !startDateTime.isBefore(endDateTime)) {
            throw new IllegalArgumentException("startDateTime must be before endDateTime");
        }

        Aircraft aircraft = aircraftRepository.findById(aircraftId)
                .orElseThrow(AircraftNotFoundException::new);
        Pilot pilot = pilotRepository.findById(pilotId)
                .orElseThrow(() -> new PilotNotFoundException(pilotId));
        OperationalBase startAirport = operationalBaseRepository.findById(startAirportId)
                .orElseThrow(() -> new OperationalBaseNotFoundException(startAirportId.toString()));
        OperationalBase endAirport = null;
        if (endAirportIdOrNull != null) {
            endAirport = operationalBaseRepository.findById(endAirportIdOrNull)
                    .orElseThrow(() -> new OperationalBaseNotFoundException(endAirportIdOrNull.toString()));
        }

        if (!flightRepository.findOverlappingByAircraft(aircraftId, startDateTime, endDateTime).isEmpty()) {
            throw new FlightTimeConflictException("Aircraft already has a flight in this time window");
        }
        if (!flightRepository.findOverlappingByPilot(pilotId, startDateTime, endDateTime).isEmpty()) {
            throw new FlightTimeConflictException("Pilot already has a flight in this time window");
        }

        OffsetDateTime monthStart = startDateTime
                .with(TemporalAdjusters.firstDayOfMonth())
                .toLocalDate()
                .atStartOfDay()
                .atOffset(startDateTime.getOffset());
        OffsetDateTime monthEnd = monthStart.plusMonths(1);
        List<Flight> contextFlights = flightRepository.findByPilotInPeriod(pilotId, monthStart, monthEnd);
        List<RuleValidator.TimeRange> ranges = contextFlights.stream()
                .map(f -> new RuleValidator.TimeRange(f.getFlightStart(), f.getFlightEnd()))
                .toList();
        List<Rule> rules = ruleRepository.findAll();
        List<RuleViolation> violations = ruleValidator.validate(pilotId, startDateTime, endDateTime, rules, ranges);
        if (!violations.isEmpty()) {
            throw new RuleViolationException(violations);
        }

        Flight flight = new Flight(
                UUID.randomUUID(),
                aircraft,
                pilot,
                startDateTime,
                endDateTime,
                startAirport,
                endAirport,
                descriptionOrNull
        );
        return flightRepository.save(flight);
    }
}
