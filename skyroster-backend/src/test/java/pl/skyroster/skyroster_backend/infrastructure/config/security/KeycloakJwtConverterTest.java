package pl.skyroster.skyroster_backend.infrastructure.config.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class KeycloakJwtConverterTest {

    private KeycloakJwtConverter converter;

    @BeforeEach
    void setUp() {
        converter = new KeycloakJwtConverter();
    }

    @Test
    void shouldMapRealmRolesToGrantedAuthorities() {
        Jwt jwt = buildJwt(Map.of(
                "realm_access", Map.of("roles", List.of("operations_administrator", "compliance_officer"))
        ));

        JwtAuthenticationToken token = (JwtAuthenticationToken) converter.convert(jwt);

        assertThat(token.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder("ROLE_operations_administrator", "ROLE_compliance_officer");
    }

    @Test
    void shouldReturnEmptyAuthoritiesWhenNoRealmAccess() {
        Jwt jwt = buildJwt(Map.of());
        JwtAuthenticationToken token = (JwtAuthenticationToken) converter.convert(jwt);
        assertThat(token.getAuthorities()).isEmpty();
    }

    @Test
    void shouldReturnEmptyAuthoritiesWhenRealmAccessHasNoRoles() {
        Jwt jwt = buildJwt(Map.of("realm_access", Map.of()));
        JwtAuthenticationToken token = (JwtAuthenticationToken) converter.convert(jwt);
        assertThat(token.getAuthorities()).isEmpty();
    }

    private Jwt buildJwt(Map<String, Object> claims) {
        return Jwt.withTokenValue("mock-token")
                .header("alg", "RS256")
                .claim("sub", "test-user")
                .claims(c -> c.putAll(claims))
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(300))
                .build();
    }
}