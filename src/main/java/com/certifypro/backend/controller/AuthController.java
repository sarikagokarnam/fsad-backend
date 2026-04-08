package com.certifypro.backend.controller;

import com.certifypro.backend.dto.*;
import com.certifypro.backend.model.User;
import com.certifypro.backend.repository.UserRepository;
import com.certifypro.backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password
    .PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    @Autowired 
    private UserRepository userRepository;
    
    @Autowired 
    private PasswordEncoder passwordEncoder;
    
    @Autowired 
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(
        @RequestBody RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail()))
            return ResponseEntity.badRequest()
                .body("Email already exists");
        User user = new User();
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(
            req.getPassword()));
        user.setRole(
            "admin".equals(req.getRole()) ? 
            "admin" : "user");
        userRepository.save(user);
        return ResponseEntity.ok(
            "User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
        @RequestBody LoginRequest req) {
        return userRepository
            .findByEmail(req.getEmail())
            .filter(u -> passwordEncoder.matches(
                req.getPassword(), u.getPassword()))
            .map(u -> {
                String token = jwtUtil.generateToken(
                    u.getEmail(), u.getRole());
                return ResponseEntity.ok(
                    new JwtResponse(
                        token,
                        u.getId(),
                        u.getName(),
                        u.getEmail(),
                        u.getRole()
                    ));
            })
            .orElse(ResponseEntity.status(401).build());
    }
}