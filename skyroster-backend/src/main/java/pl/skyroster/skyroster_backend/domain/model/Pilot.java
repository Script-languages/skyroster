package pl.skyroster.skyroster_backend.domain.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.Set;
import java.util.UUID;

@Entity
@Getter
public class Pilot {

    @Id
    private UUID id;

    private String name;
    private String surname;
    private String licence;

    @ManyToOne
    @JoinColumn(name = "operational_base_id")
    private OperationalBase operationalBase;

    @ManyToMany
    @JoinTable(
            name = "pilot_qualifications",
            joinColumns = @JoinColumn(name = "pilot_id"),
            inverseJoinColumns = @JoinColumn(name = "qualification")
    )
    private Set<Qualification> qualifications;

    @ManyToMany
    @JoinTable(
            name = "pilot_aircraft_types",
            joinColumns = @JoinColumn(name = "pilot_id"),
            inverseJoinColumns = @JoinColumn(name = "aircraft_type")
    )
    private Set<AircraftType> aircraftTypes;
}