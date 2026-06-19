package com.example.bankcards.controller;

import com.example.bankcards.security.JwtUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final JwtUtils jwtUtils;

    public AuthController(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    // Тестовый входовый метод: передаем username и роль (USER или ADMIN), получаем JWT
    @GetMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestParam String username, @RequestParam String role) {
        String token = jwtUtils.generateToken(username, role);
        return ResponseEntity.ok(Map.of("token", token));
    }
}
