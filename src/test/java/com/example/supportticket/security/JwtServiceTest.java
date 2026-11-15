package com.example.supportticket.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {
    private static final String SECRET = "test-secret-that-is-longer-than-thirty-two-characters";

    @Test
    void generatedTokenIsValidForItsUser() {
        JwtService service = new JwtService(SECRET, 3_600_000);
        var user = User.withUsername("alice").password("not-used").authorities("ROLE_USER").build();

        String token = service.generateToken(user);

        assertThat(service.extractUsername(token)).isEqualTo("alice");
        assertThat(service.isValid(token, user)).isTrue();
    }
}
