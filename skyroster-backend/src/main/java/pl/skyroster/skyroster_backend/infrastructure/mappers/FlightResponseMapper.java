package pl.skyroster.skyroster_backend.infrastructure.mappers;

import pl.skyroster.skyroster_backend.domain.model.Flight;
import pl.skyroster.skyroster_backend.generated.model.FlightResponse;

public class FlightResponseMapper {
    public static FlightResponse map(Flight flight) {
        return new FlightResponse(
                flight.getId(),
                AircraftResponseMapper.map(flight.getAircraft()),
                flight.getFlightStart(),
                flight.getFlightEnd(),
                OperationalBaseInfoMapper.map(flight.getStartAirport()),
                flight.getEndAirport() != null ? OperationalBaseInfoMapper.map(flight.getEndAirport()) : null,
                flight.getDescription()
        );
    }
}
