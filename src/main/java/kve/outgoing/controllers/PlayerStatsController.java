package kve.outgoing.controllers;

import kve.dto.Player;
import kve.incoming.services.PlayerService;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

@Async
@RestController
@RequestMapping("/api/players-stats")
public class PlayerStatsController {

    private final PlayerService playerService;

    public PlayerStatsController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping("/{name}")
    public ResponseEntity<Player> getPlayerStatsByName(@PathVariable String name) {
        Player player = playerService.getPlayerByName(name);
        if (player != null) {
            return ResponseEntity.ok(player);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
