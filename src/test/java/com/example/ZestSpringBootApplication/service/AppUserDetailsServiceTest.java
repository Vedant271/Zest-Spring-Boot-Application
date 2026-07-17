package com.example.ZestSpringBootApplication.service;

import com.example.ZestSpringBootApplication.entity.User;
import com.example.ZestSpringBootApplication.repository.UserRepository;
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
class AppUserDetailsServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AppUserDetailsService appUserDetailsService;

    @Test
    void loadUserByUsername_whenUserExists_shouldReturnUserDetails() {
        User user = User.builder()
                .id(1)
                .username("jdoe")
                .email("jdoe@example.com")
                .password("encodedPassword")
                .build();
        when(userRepository.findByUsername("jdoe")).thenReturn(Optional.of(user));

        UserDetails result = appUserDetailsService.loadUserByUsername("jdoe");

        assertThat(result.getUsername()).isEqualTo("jdoe");
        assertThat(result.getPassword()).isEqualTo("encodedPassword");
        assertThat(result.getAuthorities()).isEmpty();
    }

    @Test
    void loadUserByUsername_whenUserDoesNotExist_shouldThrow() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appUserDetailsService.loadUserByUsername("unknown"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found");
    }
}
