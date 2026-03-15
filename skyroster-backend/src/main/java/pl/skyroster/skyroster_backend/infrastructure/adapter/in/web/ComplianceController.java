package pl.skyroster.skyroster_backend.infrastructure.adapter.in.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.skyroster.skyroster_backend.generated.api.ApiApi;
import pl.skyroster.skyroster_backend.generated.model.ComplianceReport;

import java.util.List;

// TODO: Temporary stub controller — replace with real implementation
@RestController
public class ComplianceController {

    @GetMapping(ApiApi.PATH_GET_COMPLIANCE_REPORTS)
    public ResponseEntity<List<ComplianceReport>> getComplianceReports() {
        List<ComplianceReport> mockReports = List.of(
                new ComplianceReport().id("CR-001").title("Monthly flight hours compliance").status("PASSED"),
                new ComplianceReport().id("CR-002").title("Rest period compliance check").status("REVIEW")
        );
        return ResponseEntity.ok(mockReports);
    }
}