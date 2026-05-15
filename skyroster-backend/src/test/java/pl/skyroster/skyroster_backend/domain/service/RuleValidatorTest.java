package pl.skyroster.skyroster_backend.domain.service;

import org.junit.jupiter.api.Test;
import pl.skyroster.skyroster_backend.domain.model.Rule;
import pl.skyroster.skyroster_backend.domain.model.RulePeriod;
import pl.skyroster.skyroster_backend.domain.model.RuleType;
import pl.skyroster.skyroster_backend.domain.model.RuleViolation;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RuleValidatorTest {

    private static final UUID PILOT = UUID.randomUUID();

    private final RuleValidator validator = new RuleValidator();

    @Test
    void noViolations_whenNoRulesAndNoPriorFlights() {
        List<RuleViolation> result = validator.validate(
                PILOT,
                instant("2026-05-20T10:00:00Z"),
                instant("2026-05-20T13:00:00Z"),
                List.of(),
                List.of()
        );
        assertThat(result).isEmpty();
    }

    @Test
    void maxWorkTime_violated_whenSumExceedsLimit() {
        Rule maxMonth = rule("Max monthly", RuleType.MAX_WORK_TIME, 10, RulePeriod.MONTH);
        List<RuleValidator.TimeRange> existing = List.of(
                tr("2026-05-10T08:00:00Z", "2026-05-10T16:00:00Z")
        );
        List<RuleViolation> result = validator.validate(
                PILOT,
                instant("2026-05-20T10:00:00Z"),
                instant("2026-05-20T13:00:00Z"),
                List.of(maxMonth),
                existing
        );
        assertThat(result).hasSize(1);
        assertThat(result.get(0).ruleType()).isEqualTo(RuleType.MAX_WORK_TIME);
        assertThat(result.get(0).message()).contains("11");
    }

    @Test
    void maxWorkTime_notViolated_whenWithinLimit() {
        Rule maxMonth = rule("Max monthly", RuleType.MAX_WORK_TIME, 100, RulePeriod.MONTH);
        List<RuleValidator.TimeRange> existing = List.of(
                tr("2026-05-10T08:00:00Z", "2026-05-10T16:00:00Z")
        );
        List<RuleViolation> result = validator.validate(
                PILOT,
                instant("2026-05-20T10:00:00Z"),
                instant("2026-05-20T13:00:00Z"),
                List.of(maxMonth),
                existing
        );
        assertThat(result).isEmpty();
    }

    @Test
    void minRestTime_violated_whenGapBeforeIsTooShort() {
        Rule minRest = rule("Min rest", RuleType.MIN_REST_TIME, 12, RulePeriod.DAY);
        List<RuleValidator.TimeRange> existing = List.of(
                tr("2026-05-20T05:00:00Z", "2026-05-20T08:00:00Z")
        );
        List<RuleViolation> result = validator.validate(
                PILOT,
                instant("2026-05-20T10:00:00Z"),
                instant("2026-05-20T13:00:00Z"),
                List.of(minRest),
                existing
        );
        assertThat(result).hasSize(1);
        assertThat(result.get(0).ruleType()).isEqualTo(RuleType.MIN_REST_TIME);
    }

    @Test
    void minRestTime_violated_whenGapAfterIsTooShort() {
        Rule minRest = rule("Min rest", RuleType.MIN_REST_TIME, 12, RulePeriod.DAY);
        List<RuleValidator.TimeRange> existing = List.of(
                tr("2026-05-20T15:00:00Z", "2026-05-20T18:00:00Z")
        );
        List<RuleViolation> result = validator.validate(
                PILOT,
                instant("2026-05-20T10:00:00Z"),
                instant("2026-05-20T13:00:00Z"),
                List.of(minRest),
                existing
        );
        assertThat(result).hasSize(1);
        assertThat(result.get(0).ruleType()).isEqualTo(RuleType.MIN_REST_TIME);
    }

    @Test
    void minFlightTime_isIgnored() {
        Rule minFlight = rule("Min flight monthly", RuleType.MIN_FLIGHT_TIME, 100, RulePeriod.MONTH);
        List<RuleViolation> result = validator.validate(
                PILOT,
                instant("2026-05-20T10:00:00Z"),
                instant("2026-05-20T13:00:00Z"),
                List.of(minFlight),
                List.of()
        );
        assertThat(result).isEmpty();
    }

    // ---- helpers ----

    private static OffsetDateTime instant(String iso) {
        return OffsetDateTime.parse(iso).withOffsetSameInstant(ZoneOffset.UTC);
    }

    private static Rule rule(String name, RuleType type, int value, RulePeriod period) {
        return new Rule(UUID.randomUUID(), name, type, value, period, null);
    }

    private static RuleValidator.TimeRange tr(String fromIso, String toIso) {
        return new RuleValidator.TimeRange(instant(fromIso), instant(toIso));
    }
}
