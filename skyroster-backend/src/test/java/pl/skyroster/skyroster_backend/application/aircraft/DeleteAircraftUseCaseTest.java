package pl.skyroster.skyroster_backend.application.aircraft;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.skyroster.skyroster_backend.domain.exception.AircraftHasAssignedFlightsException;
import pl.skyroster.skyroster_backend.domain.exception.AircraftNotFoundException;
import pl.skyroster.skyroster_backend.domain.model.Aircraft;
import pl.skyroster.skyroster_backend.domain.model.AircraftType;
import pl.skyroster.skyroster_backend.domain.model.OperationalBase;
import pl.skyroster.skyroster_backend.domain.port.AircraftRepository;
import pl.skyroster.skyroster_backend.domain.port.FlightRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteAircraftUseCaseTest {

    @Mock private AircraftRepository aircraftRepository;
    @Mock private FlightRepository flightRepository;

    @InjectMocks private DeleteAircraftUseCase useCase;

    private UUID aircraftId;
    private Aircraft aircraft;

    @BeforeEach
    void setUp() {
        aircraftId = UUID.randomUUID();
        AircraftType type = new AircraftType(UUID.randomUUID(), "B738", "Boeing 737-800");
        OperationalBase base = new OperationalBase(UUID.randomUUID(), "EPWA", "Warsaw");
        aircraft = new Aircraft(aircraftId, "SP-DEL", LocalDateTime.now(), type, base);
    }

    @Test
    void shouldDeleteAircraftWhenNoFlightsAssigned() {
        when(aircraftRepository.findById(aircraftId)).thenReturn(Optional.of(aircraft));
        when(flightRepository.existsByAircraftId(aircraftId)).thenReturn(false);

        assertThatCode(() -> useCase.execute(aircraftId)).doesNotThrowAnyException();

        verify(aircraftRepository).deleteById(aircraftId);
    }

    @Test
    void shouldThrowAircraftNotFoundWhenIdDoesNotExist() {
        when(aircraftRepository.findById(aircraftId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(aircraftId))
                .isInstanceOf(AircraftNotFoundException.class);

        verify(aircraftRepository, never()).deleteById(any());
        verifyNoInteractions(flightRepository);
    }

    @Test
    void shouldThrowAircraftHasAssignedFlightsWhenFlightsExist() {
        when(aircraftRepository.findById(aircraftId)).thenReturn(Optional.of(aircraft));
        when(flightRepository.existsByAircraftId(aircraftId)).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(aircraftId))
                .isInstanceOf(AircraftHasAssignedFlightsException.class)
                .hasMessageContaining("SP-DEL");

        verify(aircraftRepository, never()).deleteById(any());
    }
}
