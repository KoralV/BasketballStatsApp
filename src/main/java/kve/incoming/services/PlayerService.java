package kve.incoming.services;

import kve.dto.GameStatsMsg;
import kve.dto.Player;
import kve.dto.Stats;
import kve.dto.Team;
import kve.incoming.repositories.PlayerRepository;
import org.springframework.stereotype.Service;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public void processGameStats(GameStatsMsg gameStatsMsg) {
        gameStatsMsg.getPlayers().forEach(playerEntry -> {
            //get player from DB if exists and update it. if it doesn't exist - create it.
            Player player = playerRepository.findByName(playerEntry.getName());

            if (player == null) {
                player = createNewPlayerWithStats(gameStatsMsg.getTeamName(), playerEntry);
            } else {
                updateStatsForExistingPlayer(playerEntry, player);
            }
            playerRepository.save(player);
        });
    }

    private static void updateStatsForExistingPlayer(Player playerEntry, Player player) {
        Stats existing = player.getStats();
        Stats incoming = playerEntry.getStats();

        existing.setPoints(existing.getPoints() + incoming.getPoints());
        existing.setRebounds(existing.getRebounds() + incoming.getRebounds());
        existing.setAssists(existing.getAssists() + incoming.getAssists());
        existing.setSteals(existing.getSteals() + incoming.getSteals());
        existing.setBlocks(existing.getBlocks() + incoming.getBlocks());
        existing.setTurnovers(existing.getTurnovers() + incoming.getTurnovers());
        existing.setFouls(existing.getFouls() + incoming.getFouls());
        existing.setMinutesPlayed(existing.getMinutesPlayed() + incoming.getMinutesPlayed());
    }

    private static Player createNewPlayerWithStats(Team team, Player playerEntry) {
        Player player;
        player = new Player();
        player.setName(playerEntry.getName());
        player.setTeam(team);
        player.setStats(playerEntry.getStats());
        return player;
    }

}