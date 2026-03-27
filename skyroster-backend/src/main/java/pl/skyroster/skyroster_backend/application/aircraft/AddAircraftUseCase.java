package pl.skyroster.skyroster_backend.application.aircraft;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.skyroster.skyroster_backend.domain.exception.AircraftAlreadyExistsException;
import pl.skyroster.skyroster_backend.domain.exception.AircraftTypeNotFoundException;
import pl.skyroster.skyroster_backend.domain.exception.OperationalBaseNotFoundException;
import pl.skyroster.skyroster_backend.domain.model.Aircraft;
import pl.skyroster.skyroster_backend.domain.model.AircraftType;
import pl.skyroster.skyroster_backend.domain.model.OperationalBase;
import pl.skyroster.skyroster_backend.domain.port.AircraftRepository;
import pl.skyroster.skyroster_backend.domain.port.AircraftTypeRepository;
import pl.skyroster.skyroster_backend.domain.port.OperationalBaseRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AddAircraftUseCase {

    private final AircraftRepository aircraftRepository;
    private final AircraftTypeRepository aircraftTypeRepository;
    private final OperationalBaseRepository operationalBaseRepository;

    public AddAircraftUseCase(AircraftRepository aircraftRepository,
                              AircraftTypeRepository aircraftTypeRepository,
                              OperationalBaseRepository operationalBaseRepository) {
        this.aircraftRepository = aircraftRepository;
        this.aircraftTypeRepository = aircraftTypeRepository;
        this.operationalBaseRepository = operationalBaseRepository;
    }

    @Transactional
    public Aircraft execute(String registrationNumber, UUID aircraftTypeId, UUID operationalBaseId) {
        if (registrationNumber == null || registrationNumber.isBlank()) {
            throw new IllegalArgumentException("Registration number must not be blank");
        }

        if (aircraftRepository.existsByRegistrationNumber(registrationNumber)) {
            throw new AircraftAlreadyExistsException(registrationNumber);
        }

        AircraftType type = aircraftTypeRepository.findById(aircraftTypeId)
                .orElseThrow(() -> new AircraftTypeNotFoundException(aircraftTypeId));

        OperationalBase base = operationalBaseRepository.findById(operationalBaseId)
                .orElseThrow(() -> new OperationalBaseNotFoundException(operationalBaseId));

        Aircraft aircraft = new Aircraft(UUID.randomUUID(), registrationNumber, LocalDateTime.now(), type, base);
        return aircraftRepository.save(aircraft);
    }
}
