package com.esports.calendar.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TournamentStatsDTO {
    private long liveTournaments;
    private long upcomingThisWeek;
    private int  activeRegions;
    private String totalPrizePool;   // formatted: "$2.4M"
    private long activeTournaments;
    private long tier2Events;
    private long tier3Events;
}
