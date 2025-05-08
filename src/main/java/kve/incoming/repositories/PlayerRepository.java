package kve.incoming.repositories;

import kve.dto.Player;
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
                    "INSERT INTO players (name, team, games_played) VALUES (?, ?, ?)",
                    player.getName(),
                    player.getTeam().getDisplayName(),
                    player.getGamesPlayed()
            );
        } else {
            jdbcTemplate.update(
                    "UPDATE players SET games_played = ?, team = ? WHERE name = ?",
                    player.getName(),
                    player.getTeam().getDisplayName(),
                    player.getGamesPlayed()
            );
        }
    }

    private Player mapRowToPlayer(ResultSet rs) throws SQLException {
        Player player = new Player();
        player.setName(rs.getString("name"));
        player.setTeam(Team.fromName(rs.getString("team")));
        player.setGamesPlayed(rs.getInt("games_played"));
        return player;
    }
}
