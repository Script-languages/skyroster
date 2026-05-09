package pl.skyroster.skyroster_backend.application.rules;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.skyroster.skyroster_backend.application.adapter.RuleMapper;
import pl.skyroster.skyroster_backend.domain.port.RuleRepository;

@Service
public class GetRulesUseCase {

    private final RuleRepository ruleRepository;

    public GetRulesUseCase(RuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    public Page<pl.skyroster.skyroster_backend.generated.model.RuleDto> execute(java.util.Optional<String> type, java.util.Optional<Boolean> active, Pageable pageable) {
        Page<pl.skyroster.skyroster_backend.domain.model.Rule> page = ruleRepository.findAllByTypeAndActive(type, active, pageable);
        return page.map(RuleMapper::toDto);
    }
}