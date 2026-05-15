package pl.skyroster.skyroster_backend.domain.port;

import pl.skyroster.skyroster_backend.domain.model.Rule;

import java.util.List;

public interface RuleRepository {
    List<Rule> findAll();
}
