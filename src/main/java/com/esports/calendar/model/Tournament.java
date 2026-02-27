package com.esports.calendar.model;

import com.esports.calendar.enums.TournamentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tournaments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tournament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String game;        // e.g. "Valorant", "BGMI", "Free Fire", "CS:GO", "Dota 2"

    private String gameTag;     // display label on card (often same as game)

    @Column(nullable = false)
    private String organizer;

    @Column(nullable = false)
    private String region;      // "India", "SEA", "APAC", "Global"

    @Column(nullable = false)
    private String tier;        // "Tier 1", "Tier 2", "Tier 3"

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TournamentStatus status;  // LIVE, REGISTRATION, UPCOMING

    @Column(nullable = false)
    private LocalDate date;

    private String prizePool;   // free-form: "USD 500,000", "INR 15,00,000"

    private int currentParticipants;

    private int maxParticipants;

    private String image;       // URL to cover image

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() { createdAt = updatedAt = LocalDateTime.now(); }

    @PreUpdate
    void onUpdate() { updatedAt = LocalDateTime.now(); }
}
