package pl.skyroster.skyroster_backend.infrastructure.mappers;

import org.springframework.stereotype.Component;
import pl.skyroster.skyroster_backend.domain.model.AircraftType;
import pl.skyroster.skyroster_backend.domain.model.Pilot;
import pl.skyroster.skyroster_backend.domain.model.Qualification;
import pl.skyroster.skyroster_backend.generated.model.PilotResponse;
import pl.skyroster.skyroster_backend.generated.model.PilotRequest;

import java.util.Comparator;
import java.util.UUID;
import java.util.stream.Collectors;

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
  public static Pilot toEntity(PilotRequest request){
    return new Pilot(
        UUID.randomUUID(),
        request.getFirstName(),
        request.getLastName(),
        request.getLicence(),
        OperationalBaseInfoMapper.fromInfo(request.getHomeBase()),
        request.getQualifications().stream().map(PilotQualificationMapper::fromPilotQualificationInfo).collect(Collectors.toSet()),
        request.getAircraftTypes().stream().map(AircraftTypeInfoMapper::toAircraftTypeInfo).collect(Collectors.toSet()));
  }
}