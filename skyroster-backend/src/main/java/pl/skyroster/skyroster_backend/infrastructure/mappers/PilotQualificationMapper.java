package pl.skyroster.skyroster_backend.infrastructure.mappers;

import pl.skyroster.skyroster_backend.domain.model.Qualification;
import pl.skyroster.skyroster_backend.generated.model.PilotQualificationInfo;

public class PilotQualificationMapper {
  public static PilotQualificationInfo toPilotQualificationInfo(Qualification qualification) {
    return new PilotQualificationInfo()
        .id(qualification.getId())
        .values(qualification.getValue())
        .label(qualification.getLabel());
  }
}
