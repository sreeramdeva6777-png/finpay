package com.sreeram.finpay.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(
                jwtService,
                "secretKey",
                "my-test-secret-key-that-is-long-enough"
        );
    }

    @Test
    void generateToken_extractUsername_success() {
        String email = "sreeram@gmail.com";

        String token = jwtService.generateToken(email);

        String username = jwtService.extractUsername(token);

        assertThat(token).isNotBlank();
        assertThat(username).isEqualTo(email);
    }

    @Test
    void isTokenValid_validUser_returnsTrue() {
        String email = "sreeram@gmail.com";

        String token = jwtService.generateToken(email);

        UserDetails user = org.springframework.security.core.userdetails.User
                .withUsername(email)
                .password("password")
                .authorities("USER")
                .build();

        boolean result = jwtService.isTokenValid(token, user);

        assertThat(result).isTrue();
    }

    @Test
    void isTokenValid_differentUser_returnsFalse() {
        String token = jwtService.generateToken("sreeram@gmail.com");

        UserDetails user = org.springframework.security.core.userdetails.User
                .withUsername("other@gmail.com")
                .password("password")
                .authorities("USER")
                .build();

        boolean result = jwtService.isTokenValid(token, user);

        assertThat(result).isFalse();
    }

    @Test
    void extractUsername_invalidToken_throwsException() {
        String token = "invalid-token";

        assertThatThrownBy(() -> jwtService.extractUsername(token))
                .isInstanceOf(Exception.class);
    }
}