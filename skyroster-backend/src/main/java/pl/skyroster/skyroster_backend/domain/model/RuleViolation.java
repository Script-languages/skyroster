package pl.skyroster.skyroster_backend.domain.model;

import java.util.UUID;

public record RuleViolation(UUID ruleId, String ruleName, RuleType ruleType, String message) {
}
