package com.esports.calendar.service;

import com.esports.calendar.dto.ProfileDto;
import com.esports.calendar.model.Profile;
import com.esports.calendar.model.User;
import com.esports.calendar.repository.ProfileRepository;
import com.esports.calendar.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ProfileService {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private UserRepository userRepository;

    public Optional<ProfileDto> getProfileByUserId(Long userId) {
        return profileRepository.findByUserId(userId).map(this::toDto);
    }

    @Transactional
    public ProfileDto upsertProfile(Long userId, ProfileDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Profile profile = profileRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Profile p = new Profile();
                    p.setUser(user);
                    return p;
                });
        if (dto.getBio() != null) profile.setBio(dto.getBio());
        if (dto.getPrimaryGames() != null) profile.setPrimaryGames(dto.getPrimaryGames());
        if (dto.getCareerHistory() != null) profile.setCareerHistory(dto.getCareerHistory());
        return toDto(profileRepository.save(profile));
    }

    @Transactional
    public void deleteProfile(Long userId) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found"));
        profileRepository.delete(profile);
    }

    private ProfileDto toDto(Profile p) {
        ProfileDto dto = new ProfileDto();
        dto.setBio(p.getBio());
        dto.setPrimaryGames(p.getPrimaryGames());
        dto.setCareerHistory(p.getCareerHistory());
        dto.setUpdateDate(p.getUpdateDate());
        return dto;
    }
}

