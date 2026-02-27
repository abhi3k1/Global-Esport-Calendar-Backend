package com.esports.calendar.controller;

import com.esports.calendar.dto.TournamentFilterDTO;
import com.esports.calendar.dto.TournamentResponseDTO;
import com.esports.calendar.dto.TournamentStatsDTO;
import com.esports.calendar.service.TournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tournaments")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:8081", allowCredentials = "true")
public class TournamentController {

    private final TournamentService service;

    // GET /api/tournaments?search=&game=&region=&tier=&dateRange=&page=0&size=12
    @GetMapping
    public ResponseEntity<Page<TournamentResponseDTO>> list(TournamentFilterDTO filter) {
        return ResponseEntity.ok(service.list(filter));
    }

    // GET /api/tournaments/stats
    @GetMapping("/stats")
    public ResponseEntity<TournamentStatsDTO> stats() {
        return ResponseEntity.ok(service.getStats());
    }

    // GET /api/tournaments/{id}
    @GetMapping("/{id}")
    public ResponseEntity<TournamentResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // POST /api/tournaments/{id}/register   (authenticated)
    @PostMapping("/{id}/register")
    public ResponseEntity<Void> register(@PathVariable Long id, @RequestParam Long userId) {
        service.register(id, userId);
        return ResponseEntity.ok().build();
    }

    // POST /api/tournaments/{id}/notify     (authenticated)
    @PostMapping("/{id}/notify")
    public ResponseEntity<Void> notify(@PathVariable Long id, @RequestParam Long userId) {
        service.subscribe(id, userId);
        return ResponseEntity.ok().build();
    }
}