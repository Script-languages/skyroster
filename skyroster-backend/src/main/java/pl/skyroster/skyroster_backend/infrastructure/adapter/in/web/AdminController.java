package pl.skyroster.skyroster_backend.infrastructure.adapter.in.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.skyroster.skyroster_backend.generated.api.ApiApi;
import pl.skyroster.skyroster_backend.generated.model.UserInfo;

import java.util.List;

// TODO: Temporary stub controller — replace with real implementation
@RestController
public class AdminController {

    @GetMapping(ApiApi.PATH_GET_ADMIN_USERS)
    public ResponseEntity<List<UserInfo>> getAdminUsers() {
        List<UserInfo> mockUsers = List.of(
                new UserInfo().username("admin").email("admin@skyroster.local").roles(List.of("operations_administrator")),
                new UserInfo().username("compliance").email("compliance@skyroster.local").roles(List.of("compliance_officer")),
                new UserInfo().username("schedule_planner").email("schedule_planner@skyroster.local").roles(List.of("schedule_planner"))
        );
        return ResponseEntity.ok(mockUsers);
    }
}