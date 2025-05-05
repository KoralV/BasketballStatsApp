package kve.outgoing.controllers;

import kve.dto.Player;
import kve.incoming.services.PlayerService;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

@Async
@RestController
@RequestMapping("/api/season-stats")
public class PlayerStatsController {

    private final PlayerService playerService;

    public PlayerStatsController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping("/player/{name}")
    public ResponseEntity<Player> getPlayerStatsByName(@PathVariable String name) {
        Player player = playerService.getPlayerByName(name);
        if (player != null) {
            return ResponseEntity.ok(player);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("team/{name}")
    public ResponseEntity<Object> getTeamStatsByName(@PathVariable String name) {
        //TODO: return agg stats for team
//        Stats teamStats = playerService.getTeamStats(name);
//        if (player != null) {
//            return ResponseEntity.ok(player);
//        } else {
//            return ResponseEntity.notFound().build();
//        }
        return ResponseEntity.ok().build();
    }
}
