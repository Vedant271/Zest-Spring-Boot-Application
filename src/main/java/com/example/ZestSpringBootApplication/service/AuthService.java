package com.example.ZestSpringBootApplication.service;

import com.example.ZestSpringBootApplication.dto.LoginRequest;
import com.example.ZestSpringBootApplication.dto.RegisterRequest;
import com.example.ZestSpringBootApplication.entity.User;
import com.example.ZestSpringBootApplication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public String register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        userRepository.save(user);
        return "User Registered Successfully";
    }

    public String login(LoginRequest request) {
        System.out.println("1");
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        System.out.println("2");
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        System.out.println("3");
        return "Login Successful";
    }
}
