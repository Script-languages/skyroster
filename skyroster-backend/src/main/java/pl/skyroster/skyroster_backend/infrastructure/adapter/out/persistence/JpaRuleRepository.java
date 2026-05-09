package pl.skyroster.skyroster_backend.infrastructure.adapter.out.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.skyroster.skyroster_backend.domain.model.Rule;
import pl.skyroster.skyroster_backend.domain.port.RuleRepository;

import java.util.UUID;

@Repository
public interface JpaRuleRepository extends JpaRepository<Rule, UUID>, RuleRepository {
    Page<Rule> findByRuleTypeAndActive(String ruleType, boolean active, Pageable pageable);
    Page<Rule> findByRuleType(String ruleType, Pageable pageable);
    Page<Rule> findByActive(boolean active, Pageable pageable);
}
