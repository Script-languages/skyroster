package pl.skyroster.skyroster_backend.infrastructure.adapter.out.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import pl.skyroster.skyroster_backend.domain.model.Rule;
import pl.skyroster.skyroster_backend.domain.port.RuleRepository;

import java.util.Optional;

@Component
public class RuleRepositoryAdapter implements RuleRepository {

    private final JpaRuleRepository jpa;

    public RuleRepositoryAdapter(JpaRuleRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Page<Rule> findAllByTypeAndActive(Optional<String> type, Optional<Boolean> active, Pageable pageable) {
        if (type.isPresent() && active.isPresent()) {
            return jpa.findByRuleTypeAndActive(type.get(), active.get(), pageable);
        }
        if (type.isPresent()) {
            return jpa.findByRuleType(type.get(), pageable);
        }
        if (active.isPresent()) {
            return jpa.findByActive(active.get(), pageable);
        }
        return jpa.findAll(pageable);
    }
}
