package pl.skyroster.skyroster_backend.application.rule;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.skyroster.skyroster_backend.domain.model.Rule;
import pl.skyroster.skyroster_backend.domain.port.RuleRepository;

import java.util.List;

@Service
public class GetRulesUseCase {

    private final RuleRepository ruleRepository;

    public GetRulesUseCase(RuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    @Transactional(readOnly = true)
    public List<Rule> execute() {
        return ruleRepository.findAll();
    }
}
