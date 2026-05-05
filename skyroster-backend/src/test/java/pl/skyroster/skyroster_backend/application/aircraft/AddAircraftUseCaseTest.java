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

    private static final String TYPE_CODE = "B738";
    private static final String BASE_CODE = "EPWA";
    private static final String REGISTRATION = "SP-LRA";

    private final AircraftType type = new AircraftType(UUID.randomUUID(), TYPE_CODE, "Boeing 737-800");
    private final OperationalBase base = new OperationalBase(UUID.randomUUID(), BASE_CODE, "Warsaw Chopin");

    @BeforeEach
    void setUp() {
        useCase = new AddAircraftUseCase(aircraftRepository, aircraftTypeRepository, operationalBaseRepository);
    }

    @Test
    void shouldAddAircraft_whenValidData() {
        when(aircraftRepository.existsByRegistrationNumber(REGISTRATION)).thenReturn(false);
        when(aircraftTypeRepository.findByIcaoCode(TYPE_CODE)).thenReturn(Optional.of(type));
        when(operationalBaseRepository.findByIcaoCode(BASE_CODE)).thenReturn(Optional.of(base));
        when(aircraftRepository.save(any(Aircraft.class))).thenAnswer(inv -> inv.getArgument(0));

        Aircraft result = useCase.execute(REGISTRATION, TYPE_CODE, BASE_CODE);

        assertThat(result.getRegistrationNumber()).isEqualTo(REGISTRATION);
        assertThat(result.getAircraftType()).isEqualTo(type);
        assertThat(result.getOperationalBase()).isEqualTo(base);
        assertThat(result.getId()).isNotNull();
        assertThat(result.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldThrow_whenRegistrationAlreadyExists() {
        when(aircraftRepository.existsByRegistrationNumber(REGISTRATION)).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(REGISTRATION, TYPE_CODE, BASE_CODE))
                .isInstanceOf(AircraftAlreadyExistsException.class)
                .hasMessageContaining(REGISTRATION);
    }

    @Test
    void shouldThrow_whenAircraftTypeNotFound() {
        when(aircraftRepository.existsByRegistrationNumber(REGISTRATION)).thenReturn(false);
        when(aircraftTypeRepository.findByIcaoCode(TYPE_CODE)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(REGISTRATION, TYPE_CODE, BASE_CODE))
                .isInstanceOf(AircraftTypeNotFoundException.class);
    }

    @Test
    void shouldThrow_whenOperationalBaseNotFound() {
        when(aircraftRepository.existsByRegistrationNumber(REGISTRATION)).thenReturn(false);
        when(aircraftTypeRepository.findByIcaoCode(TYPE_CODE)).thenReturn(Optional.of(type));
        when(operationalBaseRepository.findByIcaoCode(BASE_CODE)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(REGISTRATION, TYPE_CODE, BASE_CODE))
                .isInstanceOf(OperationalBaseNotFoundException.class);
    }

    @Test
    void shouldThrow_whenRegistrationIsBlank() {
        assertThatThrownBy(() -> useCase.execute("", TYPE_CODE, BASE_CODE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Registration number");
    }
}
