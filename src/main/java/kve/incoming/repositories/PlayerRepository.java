package kve.incoming.repositories;

import kve.dto.Player;
import kve.dto.Stats;
import kve.dto.Team;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class PlayerRepository {

    private final JdbcTemplate jdbcTemplate;

    public PlayerRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Player findByName(String name) {
        String sql = "SELECT * FROM players WHERE name = ?";
        return jdbcTemplate.query(sql, new Object[]{name}, rs -> {
            if (rs.next()) {
                return mapRowToPlayer(rs);
            }
            return null;
        });
    }

    public void save(Player player) {
        Player existing = findByName(player.getName());
        if (existing == null) {
            jdbcTemplate.update(
                    "INSERT INTO players (name, team, points, rebounds, assists, steals, blocks, turnovers, fouls, minutes_played) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    player.getName(),
                    player.getTeam().getDisplayName(),
                    player.getStats().getPoints(),
                    player.getStats().getRebounds(),
                    player.getStats().getAssists(),
                    player.getStats().getSteals(),
                    player.getStats().getBlocks(),
                    player.getStats().getTurnovers(),
                    player.getStats().getFouls(),
                    player.getStats().getMinutesPlayed()
            );
        } else {
            jdbcTemplate.update(
                    "UPDATE players SET points = ?, rebounds = ?, assists = ?, steals = ?, blocks = ?, turnovers = ?, fouls = ?, minutes_played = ? WHERE name = ?",
                    player.getStats().getPoints(),
                    player.getStats().getRebounds(),
                    player.getStats().getAssists(),
                    player.getStats().getSteals(),
                    player.getStats().getBlocks(),
                    player.getStats().getTurnovers(),
                    player.getStats().getFouls(),
                    player.getStats().getMinutesPlayed(),
                    player.getName()
            );
        }
    }

    private Player mapRowToPlayer(ResultSet rs) throws SQLException {
        Player player = new Player();
        player.setName(rs.getString("name"));
        player.setTeam(Team.fromName(rs.getString("team")));
        Stats stats = new Stats();
        stats.setPoints(rs.getInt("points"));
        stats.setRebounds(rs.getInt("rebounds"));
        stats.setAssists(rs.getInt("assists"));
        stats.setSteals(rs.getInt("steals"));
        stats.setBlocks(rs.getInt("blocks"));
        stats.setTurnovers(rs.getInt("turnovers"));
        stats.setFouls(rs.getInt("fouls"));
        stats.setMinutesPlayed(rs.getFloat("minutes_played"));
        player.setStats(stats);
        return player;
    }
}
