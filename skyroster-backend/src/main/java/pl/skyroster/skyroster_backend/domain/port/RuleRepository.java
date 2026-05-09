package pl.skyroster.skyroster_backend.domain.port;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.skyroster.skyroster_backend.domain.model.Rule;

import java.util.Optional;

public interface RuleRepository {
    Page<Rule> findAllByTypeAndActive(Optional<String> type, Optional<Boolean> active, Pageable pageable);
}