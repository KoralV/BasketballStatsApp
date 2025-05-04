package kve.incoming.repositories;

import kve.dto.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PlayerRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void savePlayer(Player player) {
        String sql = "INSERT INTO players (name, team, points, rebounds) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, player.getName(), player.getTeam(), player.getPoints(), player.getRebounds());
    }

    public Player getPlayerByName(String name) {
        String sql = "SELECT * FROM players WHERE name = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{name}, new PlayerRowMapper());
    }
}
