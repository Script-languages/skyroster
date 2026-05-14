package pl.skyroster.skyroster_backend.infrastructure.mappers;

import pl.skyroster.skyroster_backend.domain.model.OperationalBase;
import pl.skyroster.skyroster_backend.generated.model.OperationalBaseInfo;

public class OperationalBaseInfoMapper {
    public static OperationalBaseInfo map(OperationalBase operationalBase) {
        return new OperationalBaseInfo(
                operationalBase.getId(),
                operationalBase.getIcaoCode(),
                operationalBase.getName()
        );
    }

    public static OperationalBase fromInfo(OperationalBaseInfo operationalBase) {
        return new OperationalBase(
            operationalBase.getId(),
            operationalBase.getIcaoCode(),
            operationalBase.getName()
        );
    }
}
