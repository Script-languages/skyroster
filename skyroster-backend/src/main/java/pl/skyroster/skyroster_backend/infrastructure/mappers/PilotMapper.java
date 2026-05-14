package pl.skyroster.skyroster_backend.infrastructure.mappers;

import org.springframework.stereotype.Component;
import pl.skyroster.skyroster_backend.domain.model.AircraftType;
import pl.skyroster.skyroster_backend.domain.model.Pilot;
import pl.skyroster.skyroster_backend.domain.model.Qualification;
import pl.skyroster.skyroster_backend.generated.model.PilotResponse;

import java.util.Comparator;

@Component
public class PilotMapper {
  public static PilotResponse toResponse(Pilot pilot) {
    return new PilotResponse()
        .id(pilot.getId())
        .firstName(pilot.getName())
        .surname(pilot.getSurname())
        .licence(pilot.getLicence())
        .homeBase(OperationalBaseInfoMapper.map(pilot.getOperationalBase()))
        .qualifications(
            pilot.getQualifications()
                .stream()
                .sorted(Comparator.comparing(Qualification::getLabel))
                .map(PilotQualificationMapper::toPilotQualificationInfo)
                .toList())
        .aircraftTypes(
            pilot.getAircraftTypes()
                .stream()
                .sorted(Comparator.comparing(AircraftType::getName))
                .map(AircraftTypeInfoMapper::toAircraftTypeInfo)
                .toList());
  }
}