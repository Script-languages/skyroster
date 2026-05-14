package pl.skyroster.skyroster_backend.application.pilot;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.skyroster.skyroster_backend.domain.model.AircraftType;
import pl.skyroster.skyroster_backend.domain.model.Pilot;
import pl.skyroster.skyroster_backend.domain.model.Qualification;
import pl.skyroster.skyroster_backend.domain.port.OperationalBaseRepository;
import pl.skyroster.skyroster_backend.domain.port.PilotRepository;
import pl.skyroster.skyroster_backend.generated.model.PilotPatchRequest;
import pl.skyroster.skyroster_backend.infrastructure.mappers.AircraftTypeInfoMapper;
import pl.skyroster.skyroster_backend.infrastructure.mappers.PilotMapper;
import pl.skyroster.skyroster_backend.generated.model.PilotResponse;
import pl.skyroster.skyroster_backend.infrastructure.mappers.PilotQualificationMapper;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatchPilotUseCase {
  private final PilotRepository pilotRepository;
  private final OperationalBaseRepository operationalBaseRepository;

  @Transactional
  public PilotResponse patchPilot(UUID id, PilotPatchRequest request) {
    Pilot pilot = pilotRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Pilot not found: " + id));

    String name = request.getName();
    if (name != null) {
      pilot.setName(name);
    }
    String surname = request.getSurname();
    if (surname != null) {
      pilot.setSurname(surname);
    }
    String icaoCode = request.getOperationalBaseIcaoCode();
    if (icaoCode != null) {
      pilot.setOperationalBase(
          operationalBaseRepository.findByIcaoCode(icaoCode)
              .orElseThrow(() -> new IllegalArgumentException("Operational base not found: " + icaoCode))
      );
    }
    Set<Qualification> qualifications = request.getQualifications().stream().map(PilotQualificationMapper::fromPilotQualificationInfo).collect(Collectors.toSet());
    pilot.setQualifications(qualifications);

    Set<AircraftType> aircraftTypes = request.getAircraftTypes().stream().map(AircraftTypeInfoMapper::toAircraftTypeInfo).collect(Collectors.toSet());
    pilot.setAircraftTypes(aircraftTypes);

    return PilotMapper.toResponse(pilotRepository.save(pilot));
  }
}
