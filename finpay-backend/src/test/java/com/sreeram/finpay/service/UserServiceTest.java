package com.sreeram.finpay.service;

import com.sreeram.finpay.dto.UserResponse;
import com.sreeram.finpay.entity.User;
import com.sreeram.finpay.exception.EmailAlreadyExistsException;
import com.sreeram.finpay.exception.InvalidCredentialsException;
import com.sreeram.finpay.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserService userService;

    @AfterEach
    void tearDown() {
        clearInvocations(userRepository, authenticationManager, jwtService);
    }

    @Test
    void register_success() {
        User user = new User();
        user.setId(1L);
        user.setName("Sreeram");
        user.setEmail("sreeram@gmail.com");
        user.setPassword("password123");

        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(1L);
            return savedUser;
        });

        UserResponse response = userService.register(user);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Sreeram");
        assertThat(response.getEmail()).isEqualTo("sreeram@gmail.com");
        assertThat(response.getToken()).isNull();

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());

        User savedUser = captor.getValue();

        assertThat(savedUser.getPassword()).isNotEqualTo("password123");
        assertThat(savedUser.getPassword()).isNotBlank();
    }

    @Test
    void register_duplicateEmail_throwsException() {
        User user = new User();
        user.setName("Sreeram");
        user.setEmail("sreeram@gmail.com");
        user.setPassword("password123");

        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> userService.register(user))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessage("Email already registered");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_success() {
        String email = "sreeram@gmail.com";
        String password = "password123";
        String token = "jwt-token";

        User user = new User();
        user.setId(1L);
        user.setName("Sreeram");
        user.setEmail(email);
        user.setPassword("encoded-password");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(jwtService.generateToken(email)).thenReturn(token);

        UserResponse response = userService.login(email, password);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Sreeram");
        assertThat(response.getEmail()).isEqualTo(email);
        assertThat(response.getToken()).isEqualTo(token);

        verify(authenticationManager).authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        );
        verify(jwtService).generateToken(email);
    }

    @Test
    void login_userNotFound_throwsException() {
        String email = "sreeram@gmail.com";
        String password = "password123";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.login(email, password))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Invalid email or password");

        verify(jwtService, never()).generateToken(anyString());
    }
}