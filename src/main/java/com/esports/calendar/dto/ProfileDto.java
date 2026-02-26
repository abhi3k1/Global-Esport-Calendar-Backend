package com.esports.calendar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDto {
    private String bio;
    private String primaryGames; // comma-separated
    private String careerHistory;
    private LocalDateTime updateDate; // read-only in responses
}

