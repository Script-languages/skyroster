package pl.skyroster.skyroster_backend.infrastructure.adapter.in.web;

import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.skyroster.skyroster_backend.generated.api.ApiApi;
import pl.skyroster.skyroster_backend.generated.model.Schedule;

import java.time.LocalDate;
import java.util.List;

// TODO: Temporary stub controller — replace with real implementation
@RestController
public class PlanningController {

    @GetMapping(value = ApiApi.PATH_GET_PLANNING_SCHEDULES, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Schedule>> getPlanningSchedules() {
        LocalDate today = LocalDate.now();
        List<Schedule> mockSchedules = List.of(
                new Schedule().id("SCH-001").pilotName("Jan Kowalski").date(today),
                new Schedule().id("SCH-002").pilotName("Anna Nowak").date(today.plusDays(1)),
                new Schedule().id("SCH-003").pilotName("Piotr Wiśniewski").date(today.plusDays(2)),
                new Schedule().id("SCH-004").pilotName("Katarzyna Wójcik").date(today.plusDays(3))
        );
        return ResponseEntity.ok(mockSchedules);
    }
}
