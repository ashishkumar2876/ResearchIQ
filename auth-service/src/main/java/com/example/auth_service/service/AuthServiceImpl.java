package com.example.auth_service.service;

import com.example.auth_service.dto.LoginRequest;
import com.example.auth_service.dto.LoginResponse;
import com.example.auth_service.dto.RegisterRequest;
import com.example.auth_service.entity.User;
import com.example.auth_service.repository.UserRepository;
import com.example.auth_service.util.JwtUtil;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public String register(RegisterRequest request) {

        log.info("Register request received for email={}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration failed - user already exists: {}", request.getEmail());
            return "User already exists";
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");

        userRepository.save(user);

        log.info("User registered successfully: {}", request.getEmail());

        return "User registered successfully";
    }

    @Override
    public LoginResponse login(LoginRequest request) {

        log.info("Login attempt for email={}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail()).orElse(null);

        if (user == null) {
            log.warn("Login failed - user not found: {}", request.getEmail());
            return new LoginResponse(null, "User not found");
        }

        boolean isPasswordValid =
                passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!isPasswordValid) {
            log.warn("Login failed - invalid password for email={}", request.getEmail());
            return new LoginResponse(null, "Invalid password");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getId());

        log.info("Login successful for email={}", request.getEmail());

        return new LoginResponse(token, "Login successful");
    }
}