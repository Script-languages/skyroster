package pl.skyroster.skyroster_backend.application.aircraft;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.skyroster.skyroster_backend.domain.exception.AircraftAlreadyExistsException;
import pl.skyroster.skyroster_backend.domain.exception.AircraftNotFoundException;
import pl.skyroster.skyroster_backend.domain.exception.AircraftTypeNotFoundException;
import pl.skyroster.skyroster_backend.domain.exception.OperationalBaseNotFoundException;
import pl.skyroster.skyroster_backend.domain.model.Aircraft;
import pl.skyroster.skyroster_backend.domain.model.AircraftType;
import pl.skyroster.skyroster_backend.domain.model.OperationalBase;
import pl.skyroster.skyroster_backend.domain.port.AircraftRepository;
import pl.skyroster.skyroster_backend.domain.port.AircraftTypeRepository;
import pl.skyroster.skyroster_backend.domain.port.OperationalBaseRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateAircraftUseCaseTest {

    @Mock private AircraftRepository aircraftRepository;
    @Mock private AircraftTypeRepository aircraftTypeRepository;
    @Mock private OperationalBaseRepository operationalBaseRepository;

    @InjectMocks private UpdateAircraftUseCase useCase;

    private UUID aircraftId;
    private LocalDateTime originalCreatedAt;
    private AircraftType originalType;
    private OperationalBase originalBase;
    private AircraftType newType;
    private OperationalBase newBase;
    private Aircraft existing;

    @BeforeEach
    void setUp() {
        aircraftId = UUID.randomUUID();
        originalCreatedAt = LocalDateTime.of(2026, 1, 1, 12, 0);
        originalType = new AircraftType(UUID.randomUUID(), "B738", "Boeing 737-800");
        originalBase = new OperationalBase(UUID.randomUUID(), "EPWA", "Warsaw");
        newType = new AircraftType(UUID.randomUUID(), "A320", "Airbus A320");
        newBase = new OperationalBase(UUID.randomUUID(), "EPKK", "Krakow");
        existing = new Aircraft(aircraftId, "SP-OLD", originalCreatedAt, originalType, originalBase);
    }

    @Test
    void shouldUpdateAllFieldsAndPreserveCreatedAt() {
        when(aircraftRepository.findById(aircraftId)).thenReturn(Optional.of(existing));
        when(aircraftRepository.existsByRegistrationNumberAndIdNot("SP-NEW", aircraftId)).thenReturn(false);
        when(aircraftTypeRepository.findByIcaoCode("A320")).thenReturn(Optional.of(newType));
        when(operationalBaseRepository.findByIcaoCode("EPKK")).thenReturn(Optional.of(newBase));
        when(aircraftRepository.save(any(Aircraft.class))).thenAnswer(inv -> inv.getArgument(0));

        Aircraft result = useCase.execute(aircraftId, "SP-NEW", "A320", "EPKK");

        assertThat(result.getId()).isEqualTo(aircraftId);
        assertThat(result.getRegistrationNumber()).isEqualTo("SP-NEW");
        assertThat(result.getCreatedAt()).isEqualTo(originalCreatedAt);
        assertThat(result.getAircraftType()).isEqualTo(newType);
        assertThat(result.getOperationalBase()).isEqualTo(newBase);
    }

    @Test
    void shouldThrowAircraftNotFoundWhenIdDoesNotExist() {
        when(aircraftRepository.findById(aircraftId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(aircraftId, "SP-NEW", "A320", "EPKK"))
                .isInstanceOf(AircraftNotFoundException.class);
        verify(aircraftRepository, never()).save(any());
    }

    @Test
    void shouldThrowAircraftAlreadyExistsWhenRegistrationTaken() {
        when(aircraftRepository.findById(aircraftId)).thenReturn(Optional.of(existing));
        when(aircraftRepository.existsByRegistrationNumberAndIdNot("SP-TAKEN", aircraftId)).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(aircraftId, "SP-TAKEN", "A320", "EPKK"))
                .isInstanceOf(AircraftAlreadyExistsException.class);
        verify(aircraftRepository, never()).save(any());
    }

    @Test
    void shouldAllowSameRegistrationOnSameAircraft() {
        when(aircraftRepository.findById(aircraftId)).thenReturn(Optional.of(existing));
        when(aircraftRepository.existsByRegistrationNumberAndIdNot("SP-OLD", aircraftId)).thenReturn(false);
        when(aircraftTypeRepository.findByIcaoCode("A320")).thenReturn(Optional.of(newType));
        when(operationalBaseRepository.findByIcaoCode("EPKK")).thenReturn(Optional.of(newBase));
        when(aircraftRepository.save(any(Aircraft.class))).thenAnswer(inv -> inv.getArgument(0));

        Aircraft result = useCase.execute(aircraftId, "SP-OLD", "A320", "EPKK");

        assertThat(result.getRegistrationNumber()).isEqualTo("SP-OLD");
    }

    @Test
    void shouldThrowAircraftTypeNotFoundForUnknownType() {
        when(aircraftRepository.findById(aircraftId)).thenReturn(Optional.of(existing));
        when(aircraftRepository.existsByRegistrationNumberAndIdNot(any(), any())).thenReturn(false);
        when(aircraftTypeRepository.findByIcaoCode("XXXX")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(aircraftId, "SP-NEW", "XXXX", "EPKK"))
                .isInstanceOf(AircraftTypeNotFoundException.class);
    }

    @Test
    void shouldThrowOperationalBaseNotFoundForUnknownBase() {
        when(aircraftRepository.findById(aircraftId)).thenReturn(Optional.of(existing));
        when(aircraftRepository.existsByRegistrationNumberAndIdNot(any(), any())).thenReturn(false);
        when(aircraftTypeRepository.findByIcaoCode("A320")).thenReturn(Optional.of(newType));
        when(operationalBaseRepository.findByIcaoCode("XXXX")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(aircraftId, "SP-NEW", "A320", "XXXX"))
                .isInstanceOf(OperationalBaseNotFoundException.class);
    }

    @Test
    void shouldThrowIllegalArgumentForBlankRegistration() {
        assertThatThrownBy(() -> useCase.execute(aircraftId, "  ", "A320", "EPKK"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
