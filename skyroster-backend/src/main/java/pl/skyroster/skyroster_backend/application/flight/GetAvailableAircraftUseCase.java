package pl.skyroster.skyroster_backend.application.flight;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.skyroster.skyroster_backend.domain.model.Aircraft;
import pl.skyroster.skyroster_backend.domain.port.AircraftRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class GetAvailableAircraftUseCase {

    private final AircraftRepository aircraftRepository;

    public GetAvailableAircraftUseCase(AircraftRepository aircraftRepository) {
        this.aircraftRepository = aircraftRepository;
    }

    @Transactional(readOnly = true)
    public List<Aircraft> execute(OffsetDateTime from, OffsetDateTime to, UUID baseIdOrNull) {
        if (from == null || to == null || !from.isBefore(to)) {
            throw new IllegalArgumentException("from must be before to and both required");
        }
        return aircraftRepository.findAvailable(from, to, baseIdOrNull);
    }
}
