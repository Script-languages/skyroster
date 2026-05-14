package pl.skyroster.skyroster_backend.infrastructure.mappers;

import pl.skyroster.skyroster_backend.domain.model.AircraftType;
import pl.skyroster.skyroster_backend.generated.model.AircraftTypeInfo;

public class AircraftTypeInfoMapper {
    public static AircraftTypeInfo toAircraftTypeInfo(AircraftType aircraftType) {
        return new AircraftTypeInfo(
                aircraftType.getId(),
                aircraftType.getIcaoCode(),
                aircraftType.getName()
        );
    }
    public static AircraftType toAircraftTypeInfo(AircraftTypeInfo aircraftType) {
        return new AircraftType(
            aircraftType.getId(),
            aircraftType.getIcaoCode(),
            aircraftType.getName()
        );
    }
}
