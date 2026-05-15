package pl.skyroster.skyroster_backend.infrastructure.adapter.in.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.skyroster.skyroster_backend.application.rule.GetRulesUseCase;
import pl.skyroster.skyroster_backend.generated.api.ApiApi;
import pl.skyroster.skyroster_backend.generated.model.RuleResponse;
import pl.skyroster.skyroster_backend.infrastructure.mappers.RuleResponseMapper;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class RuleController {

    private final GetRulesUseCase getRulesUseCase;

    @GetMapping(ApiApi.PATH_GET_RULES)
    public ResponseEntity<List<RuleResponse>> getRules() {
        List<RuleResponse> response = getRulesUseCase.execute().stream()
                .map(RuleResponseMapper::map)
                .toList();
        return ResponseEntity.ok(response);
    }
}
