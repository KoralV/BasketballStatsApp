package kve.incoming.services;

import kve.dto.*;
import kve.incoming.repositories.PlayerRepository;
import kve.incoming.repositories.PlayerSeasonStatsRepository;
import kve.incoming.repositories.TeamSeasonStatsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.util.Pair;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.data.util.Pair.of;

class StatsServiceTest {

    private PlayerRepository playerRepository;
    private PlayerSeasonStatsRepository playerSeasonStatsRepository;
    private TeamSeasonStatsRepository teamSeasonStatsRepository;
    private StatsService statsService;

    @BeforeEach
    void setUp() {
        playerRepository = mock(PlayerRepository.class);
        playerSeasonStatsRepository = mock(PlayerSeasonStatsRepository.class);
        teamSeasonStatsRepository = mock(TeamSeasonStatsRepository.class);
        statsService = new StatsService(playerRepository, playerSeasonStatsRepository, teamSeasonStatsRepository);
    }

    @Test
    void testGetPlayerSeasonStats_existing() {
        PlayerSeasonStats dummyStats = new PlayerSeasonStats();
        when(playerSeasonStatsRepository.findByNameAndSeason("LeBron James", "2025")).thenReturn(dummyStats);

        PlayerSeasonStats result = statsService.getPlayerSeasonStats("LeBron James", "2025");

        assertEquals(dummyStats, result);
        verify(playerSeasonStatsRepository).findByNameAndSeason("LeBron James", "2025");
    }

    @Test
    void testGetTeamSeasonStats_existing() {
        TeamSeasonStats teamStats = new TeamSeasonStats();
        when(teamSeasonStatsRepository.findByTeamAndSeason(Team.LOS_ANGELES_LAKERS, "2025")).thenReturn(teamStats);

        TeamSeasonStats result = statsService.getTeamSeasonStats("LOS_ANGELES_LAKERS", "2025");

        assertEquals(teamStats, result);
        verify(teamSeasonStatsRepository).findByTeamAndSeason(Team.LOS_ANGELES_LAKERS, "2025");
    }

    @Test
    void testProcessGameStats_firstTimeStats() {
        // Prepare test data
        Team team = Team.GOLDEN_STATE_WARRIORS;
        Stats stats = new Stats(30, 10, 5, 2, 1, 3, 2, 35.5f);
        Pair<String, Stats> playerStats = of("Stephen Curry", stats);
        GameStatsMsg msg = new GameStatsMsg(team, List.of(playerStats));

        Player player = new Player();
        player.setName("Stephen Curry");
        player.setTeam(team);
        player.setGamesPlayed(0);

        when(playerRepository.findByName("Stephen Curry")).thenReturn(player);
        when(playerSeasonStatsRepository.findByNameAndSeason(eq("Stephen Curry"), anyString())).thenReturn(null);
        when(teamSeasonStatsRepository.findByTeamAndSeason(eq(team), anyString())).thenReturn(null);

        // Act
        statsService.processGameStats(msg);

        // Verify player stats saved
        ArgumentCaptor<PlayerSeasonStats> playerCaptor = ArgumentCaptor.forClass(PlayerSeasonStats.class);
        verify(playerSeasonStatsRepository).save(playerCaptor.capture());
        PlayerSeasonStats savedStats = playerCaptor.getValue();

        assertEquals("Stephen Curry", savedStats.getPlayerName());
        assertEquals(stats.getPoints(), savedStats.getStats().getPoints());
        assertEquals(stats.getMinutesPlayed(), savedStats.getStats().getMinutesPlayed());

        // Verify team stats saved
        verify(teamSeasonStatsRepository).save(any(TeamSeasonStats.class));
    }

    @Test
    void testProcessGameStats_updateExistingStats() {
        // Arrange
        Team team = Team.BOSTON_CELTICS;
        Stats newStats = new Stats(20, 5, 7, 1, 1, 2, 3, 32f);
        Pair<String, Stats> pair = of("Jayson Tatum", newStats);
        GameStatsMsg msg = new GameStatsMsg(team, List.of(pair));

        Player player = new Player();
        player.setName("Jayson Tatum");
        player.setTeam(team);
        player.setGamesPlayed(3);

        Stats oldStats = new Stats(21, 6, 6, 2, 2, 2, 2, 30f);
        PlayerSeasonStats existingSeasonStats = new PlayerSeasonStats();
        existingSeasonStats.setPlayerName("Jayson Tatum");
        existingSeasonStats.setSeason("2025");
        existingSeasonStats.setStats(oldStats);

        TeamSeasonStats teamStats = new TeamSeasonStats();
        teamStats.setStats(new Stats(100, 40, 30, 5, 5, 10, 15, 120f));
        teamStats.setGamesPlayed(3);

        when(playerRepository.findByName("Jayson Tatum")).thenReturn(player);
        when(playerSeasonStatsRepository.findByNameAndSeason("Jayson Tatum", "2025")).thenReturn(existingSeasonStats);
        when(teamSeasonStatsRepository.findByTeamAndSeason(team, "2025")).thenReturn(teamStats);

        // Act
        statsService.processGameStats(msg);

        // Assert player stats were updated
        verify(playerSeasonStatsRepository).save(any(PlayerSeasonStats.class));
        verify(teamSeasonStatsRepository).save(any(TeamSeasonStats.class));
    }
}
