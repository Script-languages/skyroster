package pl.skyroster.skyroster_backend.infrastructure.adapter.in.web;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.skyroster.skyroster_backend.application.rules.GetRulesUseCase;

import java.util.ArrayList;
import java.util.List;

@RestController
public class RulesController {

    private final GetRulesUseCase getRulesUseCase;

    public RulesController(GetRulesUseCase getRulesUseCase) {
        this.getRulesUseCase = getRulesUseCase;
    }

    @GetMapping("/api/v1/rules")
    @PreAuthorize("hasRole('compliance_officer')")
    public ResponseEntity<?> getRules(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Boolean active
    ) {
        Sort sortObj = Sort.unsorted();
        if (sort != null && !sort.isBlank()) {
            // Support multiple sort criteria separated by ';'
            // Each criterion can be: "field,asc" or "field:asc" or "-field"
            String[] criteria = sort.split(";");
            List<Sort.Order> orders = new ArrayList<>();
            for (String c : criteria) {
                c = c.trim();
                if (c.isEmpty()) continue;
                if (c.startsWith("-")) {
                    orders.add(Sort.Order.desc(c.substring(1)));
                } else if (c.contains(",")) {
                    String[] kv = c.split(",", 2);
                    String prop = kv[0].trim();
                    String dir = kv[1].trim().toLowerCase();
                    orders.add("desc".equals(dir) ? Sort.Order.desc(prop) : Sort.Order.asc(prop));
                } else if (c.contains(":")) {
                    String[] kv = c.split(":", 2);
                    String prop = kv[0].trim();
                    String dir = kv[1].trim().toLowerCase();
                    orders.add("desc".equals(dir) ? Sort.Order.desc(prop) : Sort.Order.asc(prop));
                } else {
                    orders.add(Sort.Order.asc(c));
                }
            }
            if (!orders.isEmpty()) sortObj = Sort.by(orders);
        }

        Pageable pageable = PageRequest.of(page, size, sortObj);
        var dtoPage = getRulesUseCase.execute(java.util.Optional.ofNullable(type), java.util.Optional.ofNullable(active), pageable);
        var response = new java.util.HashMap<String, Object>();
        response.put("content", dtoPage.getContent());
        response.put("page", dtoPage.getNumber());
        response.put("size", dtoPage.getSize());
        response.put("totalElements", dtoPage.getTotalElements());
        response.put("totalPages", dtoPage.getTotalPages());
        return ResponseEntity.ok(response);
    }
}