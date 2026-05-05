package pl.skyroster.skyroster_backend.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "operational_bases")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OperationalBase {

    @Id
    private UUID id;

    @Column(name = "icao_code", nullable = false, unique = true, length = 4)
    private String icaoCode;

    @Column(nullable = false, length = 100)
    private String name;
}
