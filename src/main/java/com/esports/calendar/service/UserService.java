package com.esports.calendar.service;

import com.esports.calendar.model.User;
import com.esports.calendar.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User createUser(User user) {
        // Prevent duplicate username/email
        userRepository.findByUsername(user.getUsername()).ifPresent(u -> {
            throw new IllegalArgumentException("Username already taken");
        });
        if (user.getEmail() != null) {
            userRepository.findByEmail(user.getEmail()).ifPresent(u -> {
                throw new IllegalArgumentException("Email already in use");
            });
        }

        // Hash password before saving
        if (user.getPassword() != null) {
            String hashed = passwordEncoder.encode(user.getPassword());
            user.setPassword(hashed);
        }

        return userRepository.save(user);
    }

    // Authenticate by email + password
    public Optional<User> authenticate(String email, String rawPassword) {
        if (email == null || rawPassword == null) return Optional.empty();
        return userRepository.findByEmail(email).filter(user -> {
            String hashed = user.getPassword();
            if (hashed == null) return false;
            return passwordEncoder.matches(rawPassword, hashed);
        });
    }

    // Authenticate by username + password
    public Optional<User> authenticateByUsername(String username, String rawPassword) {
        if (username == null || rawPassword == null) return Optional.empty();
        return userRepository.findByUsername(username).filter(user -> {
            String hashed = user.getPassword();
            if (hashed == null) return false;
            return passwordEncoder.matches(rawPassword, hashed);
        });
    }

    public User updateUser(Long id, User payload) {
        return userRepository.findById(id).map(u -> {
            u.setDisplayName(payload.getDisplayName());
            return userRepository.save(u);
        }).orElse(null);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
