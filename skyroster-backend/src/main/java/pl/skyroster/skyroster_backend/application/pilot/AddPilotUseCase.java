package pl.skyroster.skyroster_backend.application.pilot;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.skyroster.skyroster_backend.domain.exception.PilotAlreadyExistsException;
import pl.skyroster.skyroster_backend.domain.port.PilotRepository;
import pl.skyroster.skyroster_backend.generated.model.PilotRequest;
import pl.skyroster.skyroster_backend.infrastructure.mappers.PilotMapper;
import pl.skyroster.skyroster_backend.generated.model.PilotResponse;
@Service
@RequiredArgsConstructor
public class AddPilotUseCase {
  private final PilotRepository pilotRepository;

  @Transactional
  public PilotResponse addPilot(PilotRequest request) {
    if (pilotRepository.existsByLicence(request.getLicence())){
      throw new PilotAlreadyExistsException("Pilot already exists");
    }
    var pilot = PilotMapper.toEntity(request);
    return PilotMapper.toResponse(pilotRepository.save(pilot));
  }
}
