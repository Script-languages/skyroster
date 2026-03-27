package pl.skyroster.skyroster_backend.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "aircraft")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Aircraft {

    @Id
    private UUID id;

    @Column(name = "registration_number", nullable = false, unique = true, length = 15)
    private String registrationNumber;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "aircraft_type_id", nullable = false)
    private AircraftType aircraftType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "operational_base_id", nullable = false)
    private OperationalBase operationalBase;

    public Aircraft(UUID id, String registrationNumber, AircraftType aircraftType, OperationalBase operationalBase) {
        this.id = id;
        this.registrationNumber = registrationNumber;
        this.createdAt = LocalDateTime.now();
        this.aircraftType = aircraftType;
        this.operationalBase = operationalBase;
    }
}
