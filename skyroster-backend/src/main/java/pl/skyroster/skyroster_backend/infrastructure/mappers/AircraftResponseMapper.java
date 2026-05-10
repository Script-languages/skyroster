package pl.skyroster.skyroster_backend.infrastructure.mappers;

import pl.skyroster.skyroster_backend.domain.model.Aircraft;
import pl.skyroster.skyroster_backend.generated.model.AircraftResponse;

public class AircraftResponseMapper {
    public static AircraftResponse map(Aircraft aircraft) {
        return new AircraftResponse(
                aircraft.getId(),
                aircraft.getRegistrationNumber(),
                AircraftTypeInfoMapper.map(aircraft.getAircraftType()),
                OperationalBaseInfoMapper.map(aircraft.getOperationalBase())
        );
    }
}
