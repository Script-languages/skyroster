package pl.skyroster.skyroster_backend.infrastructure.mappers;

import pl.skyroster.skyroster_backend.domain.model.Rule;
import pl.skyroster.skyroster_backend.generated.model.RulePeriod;
import pl.skyroster.skyroster_backend.generated.model.RuleResponse;
import pl.skyroster.skyroster_backend.generated.model.RuleType;

public class RuleResponseMapper {
    public static RuleResponse map(Rule rule) {
        return new RuleResponse(
                rule.getId(),
                rule.getName(),
                RuleType.valueOf(rule.getType().name()),
                rule.getValue(),
                RulePeriod.valueOf(rule.getPeriod().name())
        ).description(rule.getDescription());
    }
}
