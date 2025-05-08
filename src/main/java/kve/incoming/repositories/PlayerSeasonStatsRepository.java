package kve.incoming.repositories;

import kve.dto.PlayerSeasonStats;
import kve.dto.Stats;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class PlayerSeasonStatsRepository {

    private final JdbcTemplate jdbcTemplate;

    public PlayerSeasonStatsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public PlayerSeasonStats findByNameAndSeason(String season, String name) {
        String sql = "SELECT * FROM players_season_stats WHERE player_name = ? and season = ?";
        return jdbcTemplate.query(sql, new Object[]{name}, rs -> {
            if (rs.next()) {
                return mapRowToPlayer(rs);
            }
            return null;
        });
    }

    public void save(PlayerSeasonStats playerSeasonStats) {
        PlayerSeasonStats existing = findByNameAndSeason(playerSeasonStats.getPlayerName(), playerSeasonStats.getSeason());
        if (existing == null) {
            jdbcTemplate.update(
                    "INSERT INTO players_season_stats (season, player_name, points, rebounds, assists, steals, blocks, turnovers, fouls, minutes_played) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    playerSeasonStats.getSeason(),
                    playerSeasonStats.getPlayerName(),
                    playerSeasonStats.getStats().getPoints(),
                    playerSeasonStats.getStats().getRebounds(),
                    playerSeasonStats.getStats().getAssists(),
                    playerSeasonStats.getStats().getSteals(),
                    playerSeasonStats.getStats().getBlocks(),
                    playerSeasonStats.getStats().getTurnovers(),
                    playerSeasonStats.getStats().getFouls(),
                    playerSeasonStats.getStats().getMinutesPlayed()
            );
        } else {
            jdbcTemplate.update(
                    "UPDATE players_season_stats SET points = ?, rebounds = ?, assists = ?, steals = ?, blocks = ?, turnovers = ?, fouls = ?, minutes_played = ? WHERE name = ? and season = ?",
                    playerSeasonStats.getStats().getPoints(),
                    playerSeasonStats.getStats().getRebounds(),
                    playerSeasonStats.getStats().getAssists(),
                    playerSeasonStats.getStats().getSteals(),
                    playerSeasonStats.getStats().getBlocks(),
                    playerSeasonStats.getStats().getTurnovers(),
                    playerSeasonStats.getStats().getFouls(),
                    playerSeasonStats.getStats().getMinutesPlayed(),
                    playerSeasonStats.getPlayerName()
            );
        }
    }


    private PlayerSeasonStats mapRowToPlayer(ResultSet rs) throws SQLException {
        PlayerSeasonStats playerSeasonStats = new PlayerSeasonStats();
        playerSeasonStats.setPlayerName(rs.getString("name"));
        playerSeasonStats.setSeason(rs.getString("season"));
        Stats stats = new Stats();
        stats.setPoints(rs.getInt("points"));
        stats.setRebounds(rs.getInt("rebounds"));
        stats.setAssists(rs.getInt("assists"));
        stats.setSteals(rs.getInt("steals"));
        stats.setBlocks(rs.getInt("blocks"));
        stats.setTurnovers(rs.getInt("turnovers"));
        stats.setFouls(rs.getInt("fouls"));
        stats.setMinutesPlayed(rs.getFloat("minutes_played"));
        playerSeasonStats.setStats(stats);
        return playerSeasonStats;
    }
}
