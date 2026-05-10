package pl.skyroster.skyroster_backend.infrastructure.adapter.out.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import pl.skyroster.skyroster_backend.domain.model.Rule;
import pl.skyroster.skyroster_backend.domain.port.RuleRepository;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Adapter returning a fixed set of predefined rules (in-memory).
 * This makes rules "hard-coded" instead of being read from DB.
 */
@Component
public class RuleRepositoryAdapter implements RuleRepository {

    private static final List<Rule> PREDEFINED;

    static {
        List<Rule> list = new ArrayList<>();

        // Example additional fixed rules
        list.add(new Rule(
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                "Min Rest 12h",
                "Minimum rest time between duties",
                "MIN_REST_MINUTES",
                null,
                720,
                null,
                true,
                OffsetDateTime.parse("2026-05-01T00:00:00Z"),
                null
        ));
        list.add(new Rule(
                UUID.fromString("22222222-2222-2222-2222-222222222222"),
                "Min Flights",
                "Minimum number of flights per month",
                "MIN_FLIGHTS",
                null,
                null,
                3,
                true,
                OffsetDateTime.parse("2026-05-01T00:00:00Z"),
                null
        ));
        list.add(new Rule(
                UUID.fromString("33333333-3333-3333-3333-333333333333"),
                "Max Month Hours",
                "Maximum flight hours per month",
                "MAX_MONTHLY_HOURS",
                160,   // przykładowy limit godzin
                null,
                null,
                true,
                OffsetDateTime.parse("2026-05-01T00:00:00Z"),
                null
        ));

        PREDEFINED = List.copyOf(list);
    }

    @Override
    public Page<Rule> findAllByTypeAndActive(Optional<String> type, Optional<Boolean> active, Pageable pageable) {
        List<Rule> filtered = PREDEFINED.stream()
                .filter(r -> type.map(t -> t.equalsIgnoreCase(r.getRuleType())).orElse(true))
                .filter(r -> active.map(a -> a.equals(r.isActive())).orElse(true))
                .collect(Collectors.toList());

        int total = filtered.size();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), total);
        List<Rule> pageContent = start >= total ? List.of() : filtered.subList(start, end);
        return new PageImpl<>(pageContent, pageable, total);
    }
}
