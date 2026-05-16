package pl.skyroster.skyroster_backend.domain.exception;

import pl.skyroster.skyroster_backend.domain.model.RuleViolation;

import java.util.List;

public class RuleViolationException extends RuntimeException {
    private final List<RuleViolation> violations;

    public RuleViolationException(List<RuleViolation> violations) {
        super("Rule violations detected: " + violations.size());
        this.violations = List.copyOf(violations);
    }

    public List<RuleViolation> getViolations() {
        return violations;
    }
}
