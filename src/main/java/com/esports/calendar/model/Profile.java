package com.esports.calendar.model;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.TimeZone;

@Entity
@Table(name = "profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Profile {

    @Id
    private Long id; // shared primary key with User

    @OneToOne(optional = false)
    @MapsId
    @JoinColumn(name = "id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private User user;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    // Comma-separated primary games
    @Column(name = "primary_games")
    private String primaryGames;

    @Column(name = "career_history", columnDefinition = "TEXT")
    private String careerHistory;

    @Column(name = "update_date")
    private LocalDateTime updateDate;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updateDate = LocalDateTime.now(TimeZone.getTimeZone("IST").toZoneId());
    }

}
