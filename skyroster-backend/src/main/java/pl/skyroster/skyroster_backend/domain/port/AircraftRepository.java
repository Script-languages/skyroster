package pl.skyroster.skyroster_backend.domain.port;

import pl.skyroster.skyroster_backend.domain.model.Aircraft;

import java.util.List;

public interface AircraftRepository {
    Aircraft save(Aircraft aircraft);
    List<Aircraft> findAll();
    boolean existsByRegistrationNumber(String registrationNumber);
}
