package pl.skyroster.skyroster_backend.application.aircraft;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.skyroster.skyroster_backend.domain.exception.AircraftAlreadyExistsException;
import pl.skyroster.skyroster_backend.domain.exception.AircraftTypeNotFoundException;
import pl.skyroster.skyroster_backend.domain.exception.OperationalBaseNotFoundException;
import pl.skyroster.skyroster_backend.domain.model.Aircraft;
import pl.skyroster.skyroster_backend.domain.model.AircraftType;
import pl.skyroster.skyroster_backend.domain.model.OperationalBase;
import pl.skyroster.skyroster_backend.domain.port.AircraftRepository;
import pl.skyroster.skyroster_backend.domain.port.AircraftTypeRepository;
import pl.skyroster.skyroster_backend.domain.port.OperationalBaseRepository;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddAircraftUseCaseTest {

    @Mock
    private AircraftRepository aircraftRepository;
    @Mock
    private AircraftTypeRepository aircraftTypeRepository;
    @Mock
    private OperationalBaseRepository operationalBaseRepository;

    private AddAircraftUseCase useCase;

    private static final UUID TYPE_ID = UUID.randomUUID();
    private static final UUID BASE_ID = UUID.randomUUID();
    private static final String REGISTRATION = "SP-LRA";

    private final AircraftType type = new AircraftType(TYPE_ID, "B738", "Boeing 737-800");
    private final OperationalBase base = new OperationalBase(BASE_ID, "EPWA", "Warsaw Chopin");

    @BeforeEach
    void setUp() {
        useCase = new AddAircraftUseCase(aircraftRepository, aircraftTypeRepository, operationalBaseRepository);
    }

    @Test
    void shouldAddAircraft_whenValidData() {
        when(aircraftRepository.existsByRegistrationNumber(REGISTRATION)).thenReturn(false);
        when(aircraftTypeRepository.findById(TYPE_ID)).thenReturn(Optional.of(type));
        when(operationalBaseRepository.findById(BASE_ID)).thenReturn(Optional.of(base));
        when(aircraftRepository.save(any(Aircraft.class))).thenAnswer(inv -> inv.getArgument(0));

        Aircraft result = useCase.execute(REGISTRATION, TYPE_ID, BASE_ID);

        assertThat(result.getRegistrationNumber()).isEqualTo(REGISTRATION);
        assertThat(result.getAircraftType()).isEqualTo(type);
        assertThat(result.getOperationalBase()).isEqualTo(base);
        assertThat(result.getId()).isNotNull();
        assertThat(result.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldThrow_whenRegistrationAlreadyExists() {
        when(aircraftRepository.existsByRegistrationNumber(REGISTRATION)).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(REGISTRATION, TYPE_ID, BASE_ID))
                .isInstanceOf(AircraftAlreadyExistsException.class)
                .hasMessageContaining(REGISTRATION);
    }

    @Test
    void shouldThrow_whenAircraftTypeNotFound() {
        when(aircraftRepository.existsByRegistrationNumber(REGISTRATION)).thenReturn(false);
        when(aircraftTypeRepository.findById(TYPE_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(REGISTRATION, TYPE_ID, BASE_ID))
                .isInstanceOf(AircraftTypeNotFoundException.class);
    }

    @Test
    void shouldThrow_whenOperationalBaseNotFound() {
        when(aircraftRepository.existsByRegistrationNumber(REGISTRATION)).thenReturn(false);
        when(aircraftTypeRepository.findById(TYPE_ID)).thenReturn(Optional.of(type));
        when(operationalBaseRepository.findById(BASE_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(REGISTRATION, TYPE_ID, BASE_ID))
                .isInstanceOf(OperationalBaseNotFoundException.class);
    }

    @Test
    void shouldThrow_whenRegistrationIsBlank() {
        assertThatThrownBy(() -> useCase.execute("", TYPE_ID, BASE_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Registration number");
    }
}
