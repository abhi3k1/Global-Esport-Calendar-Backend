package com.esports.calendar.dto;

import lombok.Data;

@Data
public class TournamentFilterDTO {
    private String search;                          // match against title, game, organizer
    private String game;                            // exact game name or null/empty = all
    private String region;                          // exact region or null/empty = all
    private String tier;                            // "Tier 1" / "Tier 2" / "Tier 3" or null
    private String dateRange;                       // "today" | "upcoming" | "this_week" | "this_month"
    private int page = 0;
    private int size = 12;
}