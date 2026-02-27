package com.esports.calendar.repository;

import com.esports.calendar.model.TournamentRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TournamentRegistrationRepository extends JpaRepository<TournamentRegistration, Long> {
    boolean existsByTournamentIdAndUserId(Long tournamentId, Long userId);
}
