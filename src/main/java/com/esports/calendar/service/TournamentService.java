package com.esports.calendar.service;

import com.esports.calendar.dto.TournamentFilterDTO;
import com.esports.calendar.dto.TournamentResponseDTO;
import com.esports.calendar.dto.TournamentStatsDTO;
import com.esports.calendar.enums.TournamentStatus;
import com.esports.calendar.model.Tournament;
import com.esports.calendar.model.TournamentNotification;
import com.esports.calendar.model.TournamentRegistration;
import com.esports.calendar.model.User;
import com.esports.calendar.repository.TournamentNotificationRepository;
import com.esports.calendar.repository.TournamentRegistrationRepository;
import com.esports.calendar.repository.TournamentRepository;
import com.esports.calendar.repository.UserRepository;
import com.esports.calendar.utils.TournamentSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TournamentService {

    private final TournamentRepository tournamentRepo;
    private final TournamentRegistrationRepository regRepo;
    private final TournamentNotificationRepository notifRepo;
    private final UserRepository userRepo;

    // ── List (paginated + filtered) ──────────────────────────────────
    public Page<TournamentResponseDTO> list(TournamentFilterDTO filter) {
        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize(), Sort.by("date").ascending());
        Page<Tournament> page = tournamentRepo.findAll(TournamentSpecification.withFilters(filter), pageable);
        return page.map(this::toDTO);
    }

    // ── Single detail ────────────────────────────────────────────────
    public TournamentResponseDTO getById(Long id) {
        Tournament t = tournamentRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tournament not found"));
        return toDTO(t);
    }

    // ── Stats ────────────────────────────────────────────────────────
    public TournamentStatsDTO getStats() {
        long live   = tournamentRepo.countByStatus(TournamentStatus.LIVE);
        long reg    = tournamentRepo.countByStatus(TournamentStatus.REGISTRATION);
        long tier2  = tournamentRepo.count(
                (root, q, cb) -> cb.equal(root.get("tier"), "Tier 2"));
        long tier3  = tournamentRepo.count(
                (root, q, cb) -> cb.equal(root.get("tier"), "Tier 3"));

        LocalDate today    = LocalDate.now();
        LocalDate weekEnd  = today.with(java.time.DayOfWeek.SUNDAY);
        long upcomingWeek  = tournamentRepo.countUpcomingThisWeek(today, weekEnd);

        int activeRegions  = tournamentRepo.countActiveRegions(
                List.of(TournamentStatus.LIVE, TournamentStatus.REGISTRATION, TournamentStatus.UPCOMING));

        // Simplified total prize pool formatting
        double total = tournamentRepo.sumPrizePoolNumeric();
        String totalFormatted = total >= 1_000_000
                ? String.format("$%.1fM", total / 1_000_000)
                : String.format("$%.0fK", total / 1_000);

        return TournamentStatsDTO.builder()
                .liveTournaments(live)
                .upcomingThisWeek(upcomingWeek)
                .activeRegions(activeRegions)
                .totalPrizePool(totalFormatted)
                .activeTournaments(live + reg)
                .tier2Events(tier2)
                .tier3Events(tier3)
                .build();
    }

    // ── Register ─────────────────────────────────────────────────────
    public void register(Long tournamentId, Long userId) {
        Tournament t = tournamentRepo.findById(tournamentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tournament not found"));

        if (t.getStatus() != TournamentStatus.REGISTRATION) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Registration is not open for this tournament");
        }
        if (t.getCurrentParticipants() >= t.getMaxParticipants()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tournament is full");
        }
        if (regRepo.existsByTournamentIdAndUserId(tournamentId, userId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Already registered");
        }

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        regRepo.save(TournamentRegistration.builder().tournament(t).user(user).build());

        // increment participant count
        t.setCurrentParticipants(t.getCurrentParticipants() + 1);
        tournamentRepo.save(t);
    }

    // ── Notify ───────────────────────────────────────────────────────
    public void subscribe(Long tournamentId, Long userId) {
        Tournament t = tournamentRepo.findById(tournamentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tournament not found"));

        if (notifRepo.existsByTournamentIdAndUserId(tournamentId, userId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Already subscribed");
        }

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        notifRepo.save(TournamentNotification.builder().tournament(t).user(user).build());
    }

    // ── Entity → DTO ─────────────────────────────────────────────────
    private TournamentResponseDTO toDTO(Tournament t) {
        return TournamentResponseDTO.builder()
                .id(t.getId())
                .title(t.getTitle())
                .game(t.getGame())
                .gameTag(t.getGameTag() != null ? t.getGameTag() : t.getGame())
                .organizer(t.getOrganizer())
                .region(t.getRegion())
                .tier(t.getTier())
                .status(t.getStatus().name().toLowerCase())       // "live" | "registration" | "upcoming"
                .date(t.getDate().toString())                     // "2026-02-15"
                .prizePool(t.getPrizePool())
                .participants(TournamentResponseDTO.ParticipantsDTO.builder()
                        .current(t.getCurrentParticipants())
                        .max(t.getMaxParticipants())
                        .build())
                .image(t.getImage())
                .build();
    }
}
