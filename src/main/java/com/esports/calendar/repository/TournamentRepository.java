package com.esports.calendar.repository;

import com.esports.calendar.enums.TournamentStatus;
import com.esports.calendar.model.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long>, JpaSpecificationExecutor<Tournament> {

    long countByStatus(TournamentStatus status);

    @Query("SELECT COUNT(DISTINCT t.region) FROM Tournament t WHERE t.status IN (:statuses)")
    int countActiveRegions(@Param("statuses") List<TournamentStatus> statuses);

    @Query("SELECT COUNT(t) FROM Tournament t WHERE t.status = 'UPCOMING' AND t.date BETWEEN :weekStart AND :weekEnd")
    long countUpcomingThisWeek(@Param("weekStart") LocalDate weekStart, @Param("weekEnd") LocalDate weekEnd);

    @Query("SELECT COALESCE(SUM(CAST(REGEXP_REPLACE(t.prizePool, '[^0-9.]', '', 'g') AS double)), 0) FROM Tournament t WHERE t.status IN ('LIVE','REGISTRATION','UPCOMING')")
    double sumPrizePoolNumeric();
    // NOTE: prizePool is free-form text with currency; for accurate aggregation consider
    // storing a separate numeric column `prizePoolAmount` (BigDecimal) + `prizePoolCurrency` (String).
    // The query above is a simplified illustration.
}
