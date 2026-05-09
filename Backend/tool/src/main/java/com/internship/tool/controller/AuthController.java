package com.internship.tool.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.internship.tool.entity.User;
import com.internship.tool.repository.UserRepository;
import com.internship.tool.security.JwtUtil;

import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/")
    public String home() {
        return "Backend is running securely! 🚀 Access Swagger at /swagger-ui/index.html";
    }

    @PostMapping("/register")
    public String register(@RequestBody User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username is already taken");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // Force ROLE_USER for public registration to prevent privilege escalation
        user.setRole("ROLE_USER");
        userRepository.save(user);
        return "User registered successfully";
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody User user) {
        Optional<User> dbUserOptional = userRepository.findByUsername(user.getUsername());

        if (dbUserOptional.isPresent() && passwordEncoder.matches(user.getPassword(), dbUserOptional.get().getPassword())) {
            Map<String, String> tokens = new HashMap<>();
            tokens.put("accessToken", jwtUtil.generateToken(user.getUsername(), dbUserOptional.get().getRole()));
            tokens.put("refreshToken", jwtUtil.generateRefreshToken(user.getUsername(), dbUserOptional.get().getRole()));
            return tokens;
        }

        throw new RuntimeException("Invalid credentials");
    }

    @PostMapping("/refresh")
    public Map<String, String> refresh(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        if (refreshToken != null) {
            try {
                String username = jwtUtil.extractUsername(refreshToken);
                String role = jwtUtil.extractRole(refreshToken);
                if (jwtUtil.validateToken(refreshToken, username)) {
                    Map<String, String> tokens = new HashMap<>();
                    tokens.put("accessToken", jwtUtil.generateToken(username, role));
                    return tokens;
                }
            } catch (Exception e) {
                throw new RuntimeException("Invalid refresh token");
            }
        }
        throw new RuntimeException("Refresh token is required");
    }
}