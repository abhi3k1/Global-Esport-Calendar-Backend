package com.esports.calendar.utils;

import com.esports.calendar.dto.TournamentFilterDTO;
import com.esports.calendar.model.Tournament;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TournamentSpecification {

    public static Specification<Tournament> withFilters(TournamentFilterDTO f) {
        return (root, query, cb) -> {
            List<Predicate> preds = new ArrayList<>();

            // free-text search across title, game, organizer
            if (f.getSearch() != null && !f.getSearch().isBlank()) {
                String like = "%" + f.getSearch().toLowerCase() + "%";
                preds.add(cb.or(
                        cb.like(cb.lower(root.get("title")), like),
                        cb.like(cb.lower(root.get("game")), like),
                        cb.like(cb.lower(root.get("organizer")), like)
                ));
            }

            if (f.getGame() != null && !f.getGame().isBlank() && !f.getGame().equalsIgnoreCase("All Games")) {
                preds.add(cb.equal(root.get("game"), f.getGame()));
            }

            if (f.getRegion() != null && !f.getRegion().isBlank() && !f.getRegion().equalsIgnoreCase("All Regions")) {
                // map "Southeast Asia" → "SEA" if stored that way
                String regionVal = f.getRegion().equalsIgnoreCase("Southeast Asia") ? "SEA" : f.getRegion();
                preds.add(cb.equal(root.get("region"), regionVal));
            }

            if (f.getTier() != null && !f.getTier().isBlank() && !f.getTier().equalsIgnoreCase("All Tiers")) {
                preds.add(cb.equal(root.get("tier"), f.getTier()));
            }

            if (f.getDateRange() != null && !f.getDateRange().isBlank()) {
                LocalDate today = LocalDate.now();
                switch (f.getDateRange().toLowerCase()) {
                    case "today" -> preds.add(cb.equal(root.get("date"), today));
                    case "this_week", "this week" -> {
                        LocalDate weekEnd = today.with(java.time.DayOfWeek.SUNDAY);
                        preds.add(cb.between(root.get("date"), today, weekEnd));
                    }
                    case "this_month", "this month" -> {
                        LocalDate monthEnd = today.withDayOfMonth(today.lengthOfMonth());
                        preds.add(cb.between(root.get("date"), today, monthEnd));
                    }
                    // "upcoming" = date >= today (default; no extra predicate needed unless you want to exclude past)
                    default -> preds.add(cb.greaterThanOrEqualTo(root.get("date"), today));
                }
            }

            return cb.and(preds.toArray(new Predicate[0]));
        };
    }
}