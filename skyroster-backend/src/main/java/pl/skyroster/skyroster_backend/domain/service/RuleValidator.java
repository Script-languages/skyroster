package pl.skyroster.skyroster_backend.domain.service;

import pl.skyroster.skyroster_backend.domain.model.Rule;
import pl.skyroster.skyroster_backend.domain.model.RulePeriod;
import pl.skyroster.skyroster_backend.domain.model.RuleViolation;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RuleValidator {

    public record TimeRange(OffsetDateTime from, OffsetDateTime to) {
        public long hours() {
            return Duration.between(from, to).toMinutes() / 60L;
        }
    }

    public List<RuleViolation> validate(UUID pilotId,
                                         OffsetDateTime newStart,
                                         OffsetDateTime newEnd,
                                         List<Rule> rules,
                                         List<TimeRange> existingFlights) {
        List<RuleViolation> violations = new ArrayList<>();
        long newHours = Duration.between(newStart, newEnd).toMinutes() / 60L;

        for (Rule rule : rules) {
            switch (rule.getType()) {
                case MAX_WORK_TIME -> {
                    TimeRange window = windowFor(rule.getPeriod(), newStart);
                    long sum = newHours;
                    for (TimeRange f : existingFlights) {
                        if (f.from().isBefore(window.to()) && f.to().isAfter(window.from())) {
                            sum += f.hours();
                        }
                    }
                    if (sum > rule.getValue()) {
                        violations.add(new RuleViolation(
                                rule.getId(),
                                rule.getName(),
                                rule.getType(),
                                "Pilot would exceed " + rule.getValue() + "h/" + rule.getPeriod()
                                        + " (would total " + sum + "h)"
                        ));
                    }
                }
                case MIN_REST_TIME -> {
                    boolean reported = false;
                    for (TimeRange f : existingFlights) {
                        if (reported) break;
                        if (!f.to().isAfter(newStart)) {
                            long gap = Duration.between(f.to(), newStart).toMinutes() / 60L;
                            if (gap < rule.getValue()) {
                                violations.add(new RuleViolation(
                                        rule.getId(),
                                        rule.getName(),
                                        rule.getType(),
                                        "Rest before flight is " + gap + "h, required: " + rule.getValue() + "h"
                                ));
                                reported = true;
                            }
                        }
                        if (!reported && !f.from().isBefore(newEnd)) {
                            long gap = Duration.between(newEnd, f.from()).toMinutes() / 60L;
                            if (gap < rule.getValue()) {
                                violations.add(new RuleViolation(
                                        rule.getId(),
                                        rule.getName(),
                                        rule.getType(),
                                        "Rest after flight is " + gap + "h, required: " + rule.getValue() + "h"
                                ));
                                reported = true;
                            }
                        }
                    }
                }
                case MIN_FLIGHT_TIME -> {
                    // Achievement rule — ignored at creation time.
                }
            }
        }
        return violations;
    }

    private static TimeRange windowFor(RulePeriod period, OffsetDateTime ref) {
        OffsetDateTime refUtc = ref.withOffsetSameInstant(ZoneOffset.UTC);
        return switch (period) {
            case DAY -> new TimeRange(
                    refUtc.truncatedTo(ChronoUnit.DAYS),
                    refUtc.truncatedTo(ChronoUnit.DAYS).plusDays(1)
            );
            case WEEK -> new TimeRange(
                    refUtc.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).truncatedTo(ChronoUnit.DAYS),
                    refUtc.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)).truncatedTo(ChronoUnit.DAYS).plusDays(1)
            );
            case MONTH -> new TimeRange(
                    refUtc.with(TemporalAdjusters.firstDayOfMonth()).truncatedTo(ChronoUnit.DAYS),
                    refUtc.with(TemporalAdjusters.firstDayOfNextMonth()).truncatedTo(ChronoUnit.DAYS)
            );
        };
    }
}
