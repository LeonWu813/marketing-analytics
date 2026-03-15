package com.leon.marketing_analytics.service;

import com.leon.marketing_analytics.dto.AuthResponse;
import com.leon.marketing_analytics.dto.LoginRequest;
import com.leon.marketing_analytics.dto.RegisterRequest;
import com.leon.marketing_analytics.security.JwtService;

import com.leon.marketing_analytics.entity.User;
import com.leon.marketing_analytics.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalStateException("Email already registered!");
        }
        String hashedPassword = passwordEncoder.encode(request.password());
        User user = User.builder()
                .email(request.email())
                .passwordHash(hashedPassword)
                .build();

        userRepository.save(user);
        return new AuthResponse(jwtService.generateToken(user));
    }

    public AuthResponse login(LoginRequest request) {
        var user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        return new AuthResponse(jwtService.generateToken(user));
    }
}
