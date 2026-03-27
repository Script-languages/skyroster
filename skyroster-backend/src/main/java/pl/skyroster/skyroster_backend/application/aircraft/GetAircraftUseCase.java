package pl.skyroster.skyroster_backend.application.aircraft;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.skyroster.skyroster_backend.domain.model.Aircraft;
import pl.skyroster.skyroster_backend.domain.port.AircraftRepository;

import java.util.List;

@Service
public class GetAircraftUseCase {

    private final AircraftRepository aircraftRepository;

    public GetAircraftUseCase(AircraftRepository aircraftRepository) {
        this.aircraftRepository = aircraftRepository;
    }

    @Transactional(readOnly = true)
    public List<Aircraft> execute() {
        return aircraftRepository.findAll();
    }
}
