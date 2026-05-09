package pl.skyroster.skyroster_backend.application.rules;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import pl.skyroster.skyroster_backend.domain.model.Rule;
import pl.skyroster.skyroster_backend.domain.port.RuleRepository;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GetRulesUseCaseTest {
    @Test
    void returnsMappedPage() {
        RuleRepository port = Mockito.mock(RuleRepository.class);
        var r = new Rule(UUID.randomUUID(), "TestRule", null, null, null, null, null, true, null, null);
        Mockito.when(port.findAllByTypeAndActive(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(new PageImpl<>(List.of(r)));
        var uc = new GetRulesUseCase(port);
        var page = uc.execute(java.util.Optional.empty(), java.util.Optional.empty(), PageRequest.of(0,10));
        assertEquals(1, page.getTotalElements());
    }
}