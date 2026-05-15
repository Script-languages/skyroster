package pl.skyroster.skyroster_backend.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "rules")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Rule {

    @Id
    private UUID id;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RuleType type;

    @Column(nullable = false)
    private Integer value;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private RulePeriod period;

    @Column(length = 500)
    private String description;
}
