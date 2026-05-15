package pl.skyroster.skyroster_backend.application.flight;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.skyroster.skyroster_backend.domain.model.Pilot;
import pl.skyroster.skyroster_backend.domain.port.PilotRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class GetAvailablePilotsUseCase {

    private final PilotRepository pilotRepository;

    public GetAvailablePilotsUseCase(PilotRepository pilotRepository) {
        this.pilotRepository = pilotRepository;
    }

    @Transactional(readOnly = true)
    public List<Pilot> execute(OffsetDateTime from, OffsetDateTime to, UUID aircraftTypeIdOrNull) {
        if (from == null || to == null || !from.isBefore(to)) {
            throw new IllegalArgumentException("from must be before to and both required");
        }
        return pilotRepository.findAvailable(from, to, aircraftTypeIdOrNull);
    }
}
