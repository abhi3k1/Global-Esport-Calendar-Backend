package com.esports.calendar.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TournamentResponseDTO {
    private Long id;
    private String title;
    private String game;
    private String gameTag;
    private String organizer;
    private String region;
    private String tier;
    private String status;       // "live" | "registration" | "upcoming" (lowercase)
    private String date;         // ISO format "2026-02-15"
    private String prizePool;
    private ParticipantsDTO participants;
    private String image;

    @Data @Builder @AllArgsConstructor
    public static class ParticipantsDTO {
        private int current;
        private int max;
    }
}