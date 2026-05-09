package pl.skyroster.skyroster_backend.application.aircraft;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.skyroster.skyroster_backend.domain.exception.AircraftHasAssignedFlightsException;
import pl.skyroster.skyroster_backend.domain.exception.AircraftNotFoundException;
import pl.skyroster.skyroster_backend.domain.model.Aircraft;
import pl.skyroster.skyroster_backend.domain.port.AircraftRepository;
import pl.skyroster.skyroster_backend.domain.port.FlightRepository;

import java.util.UUID;

@Service
public class DeleteAircraftUseCase {

    private final AircraftRepository aircraftRepository;
    private final FlightRepository flightRepository;

    public DeleteAircraftUseCase(AircraftRepository aircraftRepository, FlightRepository flightRepository) {
        this.aircraftRepository = aircraftRepository;
        this.flightRepository = flightRepository;
    }

    @Transactional
    public void execute(UUID id) {
        Aircraft aircraft = aircraftRepository.findById(id)
                .orElseThrow(AircraftNotFoundException::new);

        if (flightRepository.existsByAircraftId(id)) {
            throw new AircraftHasAssignedFlightsException(aircraft.getRegistrationNumber());
        }

        aircraftRepository.deleteById(id);
    }
}
