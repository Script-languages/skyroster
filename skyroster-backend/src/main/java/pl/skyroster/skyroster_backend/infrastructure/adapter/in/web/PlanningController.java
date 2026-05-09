package pl.skyroster.skyroster_backend.infrastructure.adapter.in.web;

import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.skyroster.skyroster_backend.generated.api.ApiApi;
import pl.skyroster.skyroster_backend.generated.model.Schedule;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

// TODO: Temporary stub controller — replace with real implementation
@RestController
public class PlanningController {

    @GetMapping(value = ApiApi.PATH_GET_PLANNING_SCHEDULES, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Schedule>> getPlanningSchedules() {
        OffsetDateTime baseTime = OffsetDateTime.now(ZoneOffset.UTC)
                .withHour(8)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);

        List<Schedule> mockSchedules = List.of(
                new Schedule()
                        .id("SCH-001")
                        .pilotName("Jan Kowalski")
                        .startDateTime(baseTime)
                        .endDateTime(baseTime.plusHours(3)),
                new Schedule()
                        .id("SCH-002")
                        .pilotName("Anna Nowak")
                        .startDateTime(baseTime.plusDays(1).plusHours(2))
                        .endDateTime(baseTime.plusDays(1).plusHours(5)),
                new Schedule()
                        .id("SCH-003")
                        .pilotName("Piotr Wiśniewski")
                        .startDateTime(baseTime.plusDays(2).plusHours(1))
                        .endDateTime(baseTime.plusDays(2).plusHours(4)),
                new Schedule()
                        .id("SCH-004")
                        .pilotName("Katarzyna Wójcik")
                        .startDateTime(baseTime.plusDays(3))
                        .endDateTime(baseTime.plusDays(3).plusHours(2))
        );
        return ResponseEntity.ok(mockSchedules);
    }
}
