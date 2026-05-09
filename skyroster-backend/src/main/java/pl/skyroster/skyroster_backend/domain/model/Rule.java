package pl.skyroster.skyroster_backend.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "rules")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Rule {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "rule_type")
    private String ruleType;

    @Column(name = "max_monthly_hours")
    private Integer maxMonthlyHours;

    @Column(name = "min_rest_minutes")
    private Integer minRestMinutes;

    @Column(name = "min_flights")
    private Integer minFlights;

    @Column(nullable = false)
    private boolean active = true;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;
}