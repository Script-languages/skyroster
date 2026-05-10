package pl.skyroster.skyroster_backend.application.aircraft;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.skyroster.skyroster_backend.domain.exception.AircraftAlreadyExistsException;
import pl.skyroster.skyroster_backend.domain.exception.AircraftNotFoundException;
import pl.skyroster.skyroster_backend.domain.exception.AircraftTypeNotFoundException;
import pl.skyroster.skyroster_backend.domain.exception.OperationalBaseNotFoundException;
import pl.skyroster.skyroster_backend.domain.model.Aircraft;
import pl.skyroster.skyroster_backend.domain.model.AircraftType;
import pl.skyroster.skyroster_backend.domain.model.OperationalBase;
import pl.skyroster.skyroster_backend.domain.port.AircraftRepository;
import pl.skyroster.skyroster_backend.domain.port.AircraftTypeRepository;
import pl.skyroster.skyroster_backend.domain.port.OperationalBaseRepository;

import java.util.UUID;

@Service
public class UpdateAircraftUseCase {

    private final AircraftRepository aircraftRepository;
    private final AircraftTypeRepository aircraftTypeRepository;
    private final OperationalBaseRepository operationalBaseRepository;

    public UpdateAircraftUseCase(AircraftRepository aircraftRepository,
                                 AircraftTypeRepository aircraftTypeRepository,
                                 OperationalBaseRepository operationalBaseRepository) {
        this.aircraftRepository = aircraftRepository;
        this.aircraftTypeRepository = aircraftTypeRepository;
        this.operationalBaseRepository = operationalBaseRepository;
    }

    @Transactional
    public Aircraft execute(UUID id, String registrationNumber, String aircraftTypeCode, String operationalBaseCode) {
        Aircraft.validateRegistrationNumber(registrationNumber);

        Aircraft existing = aircraftRepository.findById(id)
                .orElseThrow(AircraftNotFoundException::new);

        if (aircraftRepository.existsByRegistrationNumberAndIdNot(registrationNumber, id)) {
            throw new AircraftAlreadyExistsException(registrationNumber);
        }

        AircraftType type = aircraftTypeRepository.findByIcaoCode(aircraftTypeCode)
                .orElseThrow(() -> new AircraftTypeNotFoundException(aircraftTypeCode));

        OperationalBase base = operationalBaseRepository.findByIcaoCode(operationalBaseCode)
                .orElseThrow(() -> new OperationalBaseNotFoundException(operationalBaseCode));

        Aircraft updated = new Aircraft(existing.getId(), registrationNumber, existing.getCreatedAt(), type, base);
        return aircraftRepository.save(updated);
    }
}
