package com.example.ZestSpringBootApplication.service;

import com.example.ZestSpringBootApplication.dto.LoginRequest;
import com.example.ZestSpringBootApplication.dto.RegisterRequest;
import com.example.ZestSpringBootApplication.entity.User;
import com.example.ZestSpringBootApplication.repository.UserRepository;
import com.example.ZestSpringBootApplication.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private AppUserDetailsService userDetailsService;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("jdoe");
        registerRequest.setEmail("jdoe@example.com");
        registerRequest.setPassword("plainPassword");

        loginRequest = new LoginRequest();
        loginRequest.setUsername("jdoe");
        loginRequest.setPassword("plainPassword");
    }

    @Test
    void register_whenUsernameDoesNotExist_shouldEncodePasswordAndSaveUser() {
        when(userRepository.existsByUsername("jdoe")).thenReturn(false);
        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");

        String result = authService.register(registerRequest);

        assertThat(result).isEqualTo("User Registered Successfully");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getUsername()).isEqualTo("jdoe");
        assertThat(savedUser.getEmail()).isEqualTo("jdoe@example.com");
        assertThat(savedUser.getPassword()).isEqualTo("encodedPassword");
    }

    @Test
    void register_whenUsernameAlreadyExists_shouldThrowAndNotSave() {
        when(userRepository.existsByUsername("jdoe")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Username already exists");

        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void login_withValidCredentials_shouldAuthenticateAndReturnToken() {
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("jdoe")
                .password("encodedPassword")
                .authorities(java.util.Collections.emptyList())
                .build();

        when(userDetailsService.loadUserByUsername("jdoe")).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("mocked-jwt-token");

        String token = authService.login(loginRequest);

        assertThat(token).isEqualTo("mocked-jwt-token");

        ArgumentCaptor<UsernamePasswordAuthenticationToken> authCaptor =
                ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager).authenticate(authCaptor.capture());
        assertThat(authCaptor.getValue().getPrincipal()).isEqualTo("jdoe");
        assertThat(authCaptor.getValue().getCredentials()).isEqualTo("plainPassword");

        verify(jwtService).generateToken(userDetails);
    }

    @Test
    void login_whenAuthenticationManagerThrows_shouldPropagateAndNotGenerateToken() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new org.springframework.security.authentication.BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(org.springframework.security.authentication.BadCredentialsException.class);

        verify(jwtService, never()).generateToken(any());
    }
}
