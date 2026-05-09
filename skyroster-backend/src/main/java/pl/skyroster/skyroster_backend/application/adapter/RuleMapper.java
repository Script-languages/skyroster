package pl.skyroster.skyroster_backend.application.adapter;

import pl.skyroster.skyroster_backend.domain.model.Rule;
import pl.skyroster.skyroster_backend.generated.model.RuleDto;

public class RuleMapper {
    public static RuleDto toDto(Rule r) {
        RuleDto dto = new RuleDto();
        if (r.getId() != null) dto.setId(r.getId());
        dto.setName(r.getName());
        dto.setDescription(r.getDescription());
        dto.setRuleType(r.getRuleType());
        dto.setMaxMonthlyHours(r.getMaxMonthlyHours());
        dto.setMinRestMinutes(r.getMinRestMinutes());
        dto.setMinFlights(r.getMinFlights());
        dto.setActive(r.isActive());
        if (r.getCreatedAt() != null) dto.setCreatedAt(r.getCreatedAt());
        if (r.getUpdatedAt() != null) dto.setUpdatedAt(r.getUpdatedAt());
        return dto;
    }
}