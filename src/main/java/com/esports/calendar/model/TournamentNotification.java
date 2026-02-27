package com.esports.calendar.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "tournament_notifications",
        uniqueConstraints = @UniqueConstraint(columnNames = {"tournament_id", "user_id"}))
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class TournamentNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private LocalDateTime subscribedAt;

    @PrePersist
    void onCreate() { subscribedAt = LocalDateTime.now(); }
}