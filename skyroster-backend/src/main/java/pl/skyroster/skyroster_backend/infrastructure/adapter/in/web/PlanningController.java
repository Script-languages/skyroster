package pl.skyroster.skyroster_backend.infrastructure.adapter.in.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.skyroster.skyroster_backend.generated.api.ApiApi;
import pl.skyroster.skyroster_backend.generated.model.Schedule;

import java.time.LocalDate;
import java.util.List;

// TODO: Temporary stub controller — replace with real implementation
@RestController
public class PlanningController {

    @GetMapping(ApiApi.PATH_GET_PLANNING_SCHEDULES)
    public ResponseEntity<List<Schedule>> getPlanningSchedules() {
        List<Schedule> mockSchedules = List.of(
                new Schedule().id("SCH-001").pilotName("Jan Kowalski").date(LocalDate.of(2026, 3, 20)),
                new Schedule().id("SCH-002").pilotName("Anna Nowak").date(LocalDate.of(2026, 3, 21))
        );
        return ResponseEntity.ok(mockSchedules);
    }
}