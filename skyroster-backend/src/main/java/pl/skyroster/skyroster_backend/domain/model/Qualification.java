package pl.skyroster.skyroster_backend.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Qualification {

    @Id
    private UUID id;

    @Column(unique = true, nullable = false, length = 3)
    private String value;

    private String label;
}
