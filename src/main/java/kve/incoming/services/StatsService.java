package kve.incoming.services;

import kve.dto.*;
import kve.incoming.repositories.PlayerRepository;
import kve.incoming.repositories.PlayerSeasonStatsRepository;
import kve.incoming.repositories.TeamSeasonStatsRepository;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.List;

@Service
public class StatsService {

    private final PlayerRepository playerRepository;
    private final PlayerSeasonStatsRepository playerSeasonStatsRepository;
    private final TeamSeasonStatsRepository teamSeasonStatsRepository;

    public StatsService(PlayerRepository playerRepository, PlayerSeasonStatsRepository playerSeasonStatsRepository, TeamSeasonStatsRepository teamSeasonStatsRepository) {
        this.playerRepository = playerRepository;
        this.playerSeasonStatsRepository = playerSeasonStatsRepository;
        this.teamSeasonStatsRepository = teamSeasonStatsRepository;
    }

    public PlayerSeasonStats getPlayerSeasonStats(String name, String season) {
        return playerSeasonStatsRepository.findByNameAndSeason(name, season);
    }

    public TeamSeasonStats getTeamSeasonStats(String name, String season) {
        return teamSeasonStatsRepository.findByTeamAndSeason(Team.valueOf(name), season);
    }

    public void processGameStats(GameStatsMsg gameStatsMsg) {
        String season = getCurrentSeason();

        TeamSeasonStats teamSeasonStats = getOrCreateTeamSeasonStats(gameStatsMsg, season);

        List<Stats> playersStatsForSeason = gameStatsMsg.getPlayersStats()
                .stream()
                .map(playerRecord -> calculatePlayerStats(playerRecord, gameStatsMsg.getTeamName(), season))
                .toList();

        Stats gameStats = aggregatePlayerStats(playersStatsForSeason);
        teamSeasonStats.setStats(recalculateAvg(teamSeasonStats.getGamesPlayed(), teamSeasonStats.getStats(), gameStats));
        teamSeasonStatsRepository.save(teamSeasonStats);
    }

    private static Stats aggregatePlayerStats(List<Stats> playersStatsForSeason) {
        Stats gameStats = new Stats();
        gameStats.setPoints(playersStatsForSeason.stream().mapToInt(Stats::getPoints).sum());
        gameStats.setRebounds(playersStatsForSeason.stream().mapToInt(Stats::getRebounds).sum());
        gameStats.setAssists(playersStatsForSeason.stream().mapToInt(Stats::getAssists).sum());
        gameStats.setSteals(playersStatsForSeason.stream().mapToInt(Stats::getSteals).sum());
        gameStats.setBlocks(playersStatsForSeason.stream().mapToInt(Stats::getBlocks).sum());
        gameStats.setTurnovers(playersStatsForSeason.stream().mapToInt(Stats::getTurnovers).sum());
        gameStats.setFouls(playersStatsForSeason.stream().mapToInt(Stats::getFouls).sum());
        gameStats.setMinutesPlayed((float) playersStatsForSeason.stream().mapToDouble(Stats::getMinutesPlayed).sum());
        return gameStats;
    }

    private Stats calculatePlayerStats(Pair<String, Stats> playerRecord, Team team, String season) {
        Player player = gerOrCreatePlayer(team, playerRecord.getFirst());
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
            player.setGamesPlayed(player.getGamesPlayed() + 1);
            playerRepository.save(player);
            playerSeasonStats.setStats(recalculatePlayerSeasonStats(playerRecord.getSecond(), playerSeasonStats.getStats(), player));
        }
        playerSeasonStatsRepository.save(playerSeasonStats);
        return playerSeasonStats.getStats();

    }

    private TeamSeasonStats getOrCreateTeamSeasonStats(GameStatsMsg gameStatsMsg, String season) {
        TeamSeasonStats teamSeasonStats = teamSeasonStatsRepository.findByTeamAndSeason(gameStatsMsg.getTeamName(), season);
        if (teamSeasonStats == null) {
            teamSeasonStats = new TeamSeasonStats();
            teamSeasonStats.setTeamName(gameStatsMsg.getTeamName());
            teamSeasonStats.setSeason(season);
            teamSeasonStats.setGamesPlayed(1);
        } else {
            teamSeasonStats.setGamesPlayed(teamSeasonStats.getGamesPlayed() + 1);
        }
        return teamSeasonStats;
    }

    private Stats recalculatePlayerSeasonStats(Stats incoming, Stats existing, Player player) {
        return recalculateAvg(player.getGamesPlayed(), existing, incoming);
    }

    private Stats recalculateAvg(int divider, Stats existing, Stats incoming) {
        Stats recalculatedStats = new Stats();
        recalculatedStats.setPoints((int) newAvg(existing.getPoints(), incoming.getPoints(), divider));
        recalculatedStats.setRebounds((int) newAvg(existing.getRebounds(), incoming.getRebounds(), divider));
        recalculatedStats.setAssists((int) newAvg(existing.getAssists(), incoming.getAssists(), divider));
        recalculatedStats.setSteals((int) newAvg(existing.getSteals(), incoming.getSteals(), divider));
        recalculatedStats.setBlocks((int) newAvg(existing.getBlocks(), incoming.getBlocks(), divider));
        recalculatedStats.setTurnovers((int) newAvg(existing.getTurnovers(), incoming.getTurnovers(), divider));
        recalculatedStats.setFouls((int) newAvg(existing.getFouls(), incoming.getFouls(), divider));
        recalculatedStats.setMinutesPlayed((float) newAvg(existing.getMinutesPlayed(), incoming.getMinutesPlayed(), divider));
        return recalculatedStats;
    }


    private Player gerOrCreatePlayer(Team team, String playerName) {
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