package pl.skyroster.skyroster_backend.application.pilot;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.skyroster.skyroster_backend.domain.exception.PilotNotFoundException;
import pl.skyroster.skyroster_backend.domain.port.FlightRepository;
import pl.skyroster.skyroster_backend.domain.port.PilotRepository;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeletePilotUseCase {

  private final PilotRepository pilotRepository;
  private final FlightRepository flightRepository;

  @Transactional
  public void deletePilotById(UUID pilotId) {
    if (!pilotRepository.existsById(pilotId)) {
      throw new PilotNotFoundException(pilotId);
    }

    if(pilotHasScheduledFlights(pilotId))
      throw new IllegalStateException("Pilot has ongoing or incoming flights");

    pilotRepository.deleteById(pilotId);
  }

  private boolean pilotHasScheduledFlights(UUID pilotId){
    return flightRepository.existsByPilotIdAndFlightEndAfter(pilotId, OffsetDateTime.now());
  }
}
