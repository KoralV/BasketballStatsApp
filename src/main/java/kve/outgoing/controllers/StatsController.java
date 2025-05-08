package kve.outgoing.controllers;

import kve.dto.PlayerSeasonStats;
import kve.dto.TeamSeasonStats;
import kve.incoming.services.StatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@Async
@RestController
@RequestMapping("/api/season-stats")
public class StatsController {

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/player/{name}/{season}")
    public CompletableFuture<ResponseEntity<PlayerSeasonStats>> getPlayerStatsByNameAndSeason(@PathVariable String name, @PathVariable String season) {
        PlayerSeasonStats playerStats = statsService.getPlayerSeasonStats(name, season);
        if (playerStats != null) {
            return CompletableFuture.completedFuture(ResponseEntity.ok(playerStats));
        } else {
            return CompletableFuture.completedFuture(ResponseEntity.notFound().build());
        }
    }

    @GetMapping("/team/{name}/{season}")
    public CompletableFuture<ResponseEntity<TeamSeasonStats>> getTeamStatsByNameAndSeason(@PathVariable String name, @PathVariable String season) {
        TeamSeasonStats teamStats = statsService.getTeamSeasonStats(name, season);
        if (teamStats != null) {
            return CompletableFuture.completedFuture(ResponseEntity.ok(teamStats));
        } else {
            return CompletableFuture.completedFuture(ResponseEntity.notFound().build());
        }
    }
}
