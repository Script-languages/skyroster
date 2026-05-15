package pl.skyroster.skyroster_backend.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.skyroster.skyroster_backend.domain.model.Rule;
import pl.skyroster.skyroster_backend.domain.port.RuleRepository;

import java.util.UUID;

public interface JpaRuleRepository extends JpaRepository<Rule, UUID>, RuleRepository {
}
