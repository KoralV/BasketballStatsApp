package kve.incoming.repositories;

import kve.dto.Stats;
import kve.dto.Team;
import kve.dto.TeamSeasonStats;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class TeamSeasonStatsRepository {

    private final JdbcTemplate jdbcTemplate;

    public TeamSeasonStatsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public TeamSeasonStats findByTeamAndSeason(Team team, String season) {
        String sql = "SELECT * FROM teams_season_stats WHERE team_name = ? AND season = ?";
        return jdbcTemplate.query(sql, new Object[]{team.name(), season}, rs -> {
            if (rs.next()) {
                return mapRowToTeam(rs);
            }
            return null;
        });
    }

    public void save(TeamSeasonStats teamSeasonStats) {
        TeamSeasonStats existing = findByTeamAndSeason(teamSeasonStats.getTeamName(), teamSeasonStats.getSeason());
        if (existing == null) {
            jdbcTemplate.update(
                    "INSERT INTO teams_season_stats (team_name, season, games_played, points, rebounds, assists, steals, blocks, turnovers, fouls, minutes_played) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    teamSeasonStats.getTeamName().name(),
                    teamSeasonStats.getSeason(),
                    teamSeasonStats.getGamesPlayed(),
                    teamSeasonStats.getStats().getPoints(),
                    teamSeasonStats.getStats().getRebounds(),
                    teamSeasonStats.getStats().getAssists(),
                    teamSeasonStats.getStats().getSteals(),
                    teamSeasonStats.getStats().getBlocks(),
                    teamSeasonStats.getStats().getTurnovers(),
                    teamSeasonStats.getStats().getFouls(),
                    teamSeasonStats.getStats().getMinutesPlayed()
            );
        } else {
            jdbcTemplate.update(
                    "UPDATE teams_season_stats SET games_played = ?, points = ?, rebounds = ?, assists = ?, steals = ?, blocks = ?, turnovers = ?, fouls = ?, minutes_played = ? WHERE team_name = ? AND season = ?",
                    teamSeasonStats.getGamesPlayed(),
                    teamSeasonStats.getStats().getPoints(),
                    teamSeasonStats.getStats().getRebounds(),
                    teamSeasonStats.getStats().getAssists(),
                    teamSeasonStats.getStats().getSteals(),
                    teamSeasonStats.getStats().getBlocks(),
                    teamSeasonStats.getStats().getTurnovers(),
                    teamSeasonStats.getStats().getFouls(),
                    teamSeasonStats.getStats().getMinutesPlayed(),
                    teamSeasonStats.getTeamName().name(),
                    teamSeasonStats.getSeason()
            );
        }
    }

    private TeamSeasonStats mapRowToTeam(ResultSet rs) throws SQLException {
        TeamSeasonStats teamSeasonStats = new TeamSeasonStats();
        teamSeasonStats.setTeamName(Team.valueOf(rs.getString("team_name")));
        teamSeasonStats.setSeason(rs.getString("season"));
        teamSeasonStats.setGamesPlayed(rs.getInt("games_played"));

        Stats stats = new Stats();
        stats.setPoints(rs.getInt("points"));
        stats.setRebounds(rs.getInt("rebounds"));
        stats.setAssists(rs.getInt("assists"));
        stats.setSteals(rs.getInt("steals"));
        stats.setBlocks(rs.getInt("blocks"));
        stats.setTurnovers(rs.getInt("turnovers"));
        stats.setFouls(rs.getInt("fouls"));
        stats.setMinutesPlayed(rs.getFloat("minutes_played"));

        teamSeasonStats.setStats(stats);
        return teamSeasonStats;
    }
}
