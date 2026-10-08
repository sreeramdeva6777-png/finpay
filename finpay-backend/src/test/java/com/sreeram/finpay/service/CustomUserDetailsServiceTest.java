package com.sreeram.finpay.service;

import com.sreeram.finpay.entity.User;
import com.sreeram.finpay.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    @Test
    void loadUserByUsername_success() {
        User user = new User();
        user.setId(1L);
        user.setName("Sreeram");
        user.setEmail("sreeram@gmail.com");
        user.setPassword("encoded-password");

        when(userRepository.findByEmail("sreeram@gmail.com"))
                .thenReturn(Optional.of(user));

        UserDetails result =
                userDetailsService.loadUserByUsername("sreeram@gmail.com");

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("sreeram@gmail.com");
        assertThat(result.getPassword()).isEqualTo("encoded-password");
    }

    @Test
    void loadUserByUsername_userNotFound_throwsException() {
        when(userRepository.findByEmail("unknown@gmail.com"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                userDetailsService.loadUserByUsername("unknown@gmail.com"))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}