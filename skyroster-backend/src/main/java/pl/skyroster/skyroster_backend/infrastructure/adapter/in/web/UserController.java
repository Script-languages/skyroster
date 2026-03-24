package pl.skyroster.skyroster_backend.infrastructure.adapter.in.web;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.skyroster.skyroster_backend.generated.api.ApiApi;
import pl.skyroster.skyroster_backend.generated.model.UserInfo;

import java.util.List;
import java.util.Objects;

@RestController
public class UserController {

    @GetMapping(ApiApi.PATH_GET_CURRENT_USER)
    public ResponseEntity<UserInfo> getCurrentUser(JwtAuthenticationToken authentication) {
        var jwt = authentication.getToken();

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).filter(Objects::nonNull)
                .map(a -> a.replace("ROLE_", ""))
                .toList();

        var userInfo = new UserInfo()
                .username(jwt.getClaimAsString("preferred_username"))
                .email(jwt.getClaimAsString("email"))
                .roles(roles);

        return ResponseEntity.ok(userInfo);
    }
}