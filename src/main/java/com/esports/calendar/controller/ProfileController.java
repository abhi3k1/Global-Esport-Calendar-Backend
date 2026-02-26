package com.esports.calendar.controller;

import com.esports.calendar.dto.ProfileDto;
import com.esports.calendar.service.JwtUtil;
import com.esports.calendar.service.ProfileService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:8081")
@RequestMapping("/api/users/{userId}/profile")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private JwtUtil jwtUtil;

    // --- helper: extract and validate bearer token, returns userId or null ---
    private Long resolveAuthenticatedUserId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
        String token = authHeader.substring(7);
        return jwtUtil.validateAndGetUserId(token);
    }

    @GetMapping
    public ResponseEntity<ProfileDto> getProfile(@PathVariable Long userId,
                                                 HttpServletRequest request) {
        Long authUserId = resolveAuthenticatedUserId(request);
        if (authUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return profileService.getProfileByUserId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping
    public ResponseEntity<ProfileDto> upsertProfile(@PathVariable Long userId,
                                                    @RequestBody ProfileDto dto,
                                                    HttpServletRequest request) {
        Long authUserId = resolveAuthenticatedUserId(request);
        if (authUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!authUserId.equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            ProfileDto result = profileService.upsertProfile(userId, dto);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteProfile(@PathVariable Long userId,
                                              HttpServletRequest request) {
        Long authUserId = resolveAuthenticatedUserId(request);
        if (authUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!authUserId.equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            profileService.deleteProfile(userId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}


