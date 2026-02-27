package com.esports.calendar.controller;

import com.esports.calendar.dto.LoginRequest;
import com.esports.calendar.dto.LoginResponse;
import com.esports.calendar.model.User;
import com.esports.calendar.service.JwtUtil;
import com.esports.calendar.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:8081")
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> u = userService.getUserById(id);
        return u.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/signup")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        try {
            User created = userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req) {
        if (req == null || req.getPassword() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        Optional<User> userOpt;

        // Support login by email OR username
        if (req.getEmail() != null && !req.getEmail().isBlank()) {
            userOpt = userService.authenticate(req.getEmail(), req.getPassword());
        } else if (req.getUsername() != null && !req.getUsername().isBlank()) {
            userOpt = userService.authenticateByUsername(req.getUsername(), req.getPassword());
        } else {
            // Neither email nor username provided
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        return userOpt.map(user -> {
            String token = jwtUtil.generateToken(user.getId());
            LoginResponse resp = new LoginResponse(
                    user.getId(),
                    user.getUsername(),
                    user.getDisplayName(),
                    user.getEmail(),
                    token
            );
            return ResponseEntity.ok(resp);
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null));
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User payload) {
        User updated = userService.updateUser(id, payload);
        if (updated != null) return ResponseEntity.ok(updated);
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
