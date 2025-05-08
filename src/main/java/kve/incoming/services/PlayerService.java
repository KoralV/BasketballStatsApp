package kve.incoming.services;

import kve.dto.*;
import kve.incoming.repositories.PlayerRepository;
import kve.incoming.repositories.PlayerSeasonStatsRepository;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.time.Year;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final PlayerSeasonStatsRepository playerSeasonStatsRepository;

    public PlayerService(PlayerRepository playerRepository, PlayerSeasonStatsRepository playerSeasonStatsRepository) {
        this.playerRepository = playerRepository;
        this.playerSeasonStatsRepository = playerSeasonStatsRepository;
    }

    public Player getPlayerByName(String name) {
        return playerRepository.findByName(name);
    }

    public void processGameStats(GameStatsMsg gameStatsMsg) {
        gameStatsMsg.getPlayersStats().stream().forEach(playerRecord -> {

            Player player = getExistingPlayerOrCreateNew(gameStatsMsg.getTeamName(), playerRecord.getFirst());
            String season = getCurrentSeason();

            // calculate player stats
            PlayerSeasonStats playerSeasonStats = playerSeasonStatsRepository.findByNameAndSeason(player.getName(), season);
            if (playerSeasonStats == null) {
                //first game for the season - reset games for the player
                player.setGamesPlayed(1);
                playerRepository.save(player);

                playerSeasonStats = new PlayerSeasonStats();
                playerSeasonStats.setPlayerName(player.getName());
                playerSeasonStats.setSeason(season);
                playerSeasonStats.setStats(playerRecord.getSecond());
            } else {
                recalculatePlayerSeasonStats(playerRecord, playerSeasonStats, player);
            }
            playerSeasonStatsRepository.save(playerSeasonStats);

            //calculate team stats


        });
    }

    private void recalculatePlayerSeasonStats(Pair<String, Stats> playerRecord, PlayerSeasonStats playerSeasonStats, Player player) {
        Stats incoming = playerRecord.getSecond();
        Stats existing = playerSeasonStats.getStats();
        existing.setPoints((int) newAvg(existing.getPoints(), incoming.getPoints(), player.getGamesPlayed()));
        existing.setRebounds((int) newAvg(existing.getRebounds(), incoming.getRebounds(), player.getGamesPlayed()));
        existing.setAssists((int) newAvg(existing.getAssists(), incoming.getAssists(), player.getGamesPlayed()));
        existing.setSteals((int) newAvg(existing.getSteals(), incoming.getSteals(), player.getGamesPlayed()));
        existing.setBlocks((int) newAvg(existing.getBlocks(), incoming.getBlocks(), player.getGamesPlayed()));
        existing.setTurnovers((int) newAvg(existing.getTurnovers(), incoming.getTurnovers(), player.getGamesPlayed()));
        existing.setFouls((int) newAvg(existing.getFouls(), incoming.getFouls(), player.getGamesPlayed()));
        existing.setMinutesPlayed((float) newAvg(existing.getMinutesPlayed(), incoming.getMinutesPlayed(), player.getGamesPlayed()));
    }


    private Player getExistingPlayerOrCreateNew(Team team, String playerName) {
        Player player = playerRepository.findByName(playerName);

        if (player == null) {
            player = createNewPlayer(team, playerName);
        }
        return player;
    }

    private Player createNewPlayer(Team team, String playerName) {
        Player player;
        player = new Player();
        player.setName(playerName);
        player.setTeam(team);
        player.setGamesPlayed(1);
        playerRepository.save(player);
        return player;
    }


    //assuming the current season is always the latest one and represented by the year - can be changed later
    private String getCurrentSeason() {
        return String.valueOf(Year.now().getValue());
    }

    private double newAvg(double oldAvg, double newVal, int count) {
        return (oldAvg * count + newVal) / (count + 1);
    }

}