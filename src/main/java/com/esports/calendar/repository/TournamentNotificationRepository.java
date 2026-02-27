package com.esports.calendar.repository;

import com.esports.calendar.model.TournamentNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TournamentNotificationRepository extends JpaRepository<TournamentNotification, Long> {
    boolean existsByTournamentIdAndUserId(Long tournamentId, Long userId);
}
