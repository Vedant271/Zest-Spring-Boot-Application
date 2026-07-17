package com.example.ZestSpringBootApplication.security;

import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {
    private static final String TEST_SECRET = "0123456789abcdef0123456789abcdef";

    private JwtService jwtService;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtService, "expiration", 3600000L); // 1 hour

        userDetails = org.springframework.security.core.userdetails.User
                .withUsername("jdoe")
                .password("irrelevant")
                .authorities(Collections.emptyList())
                .build();
    }

    @Test
    void generateToken_shouldProduceNonBlankToken() {
        String token = jwtService.generateToken(userDetails);

        assertThat(token).isNotBlank();

        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    void extractUsername_shouldReturnSubjectFromToken() {
        String token = jwtService.generateToken(userDetails);

        String username = jwtService.extractUsername(token);

        assertThat(username).isEqualTo("jdoe");
    }

    @Test
    void isTokenValid_withMatchingUserAndUnexpiredToken_shouldReturnTrue() {
        String token = jwtService.generateToken(userDetails);

        boolean valid = jwtService.isTokenValid(token, userDetails);

        assertThat(valid).isTrue();
    }

    @Test
    void isTokenValid_withDifferentUsername_shouldReturnFalse() {
        String token = jwtService.generateToken(userDetails);
        UserDetails otherUser = org.springframework.security.core.userdetails.User
                .withUsername("someoneElse")
                .password("irrelevant")
                .authorities(Collections.emptyList())
                .build();

        boolean valid = jwtService.isTokenValid(token, otherUser);

        assertThat(valid).isFalse();
    }

    @Test
    void extractUsername_withTamperedToken_shouldThrow() {
        String token = jwtService.generateToken(userDetails);
        String tampered = token.substring(0, token.length() - 2) + "xx";

        assertThatThrownBy(() -> jwtService.extractUsername(tampered))
                .isInstanceOf(io.jsonwebtoken.security.SignatureException.class);
    }
}
