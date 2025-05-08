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

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        // Given
        Player existingPlayer = new Player();
        existingPlayer.setName("John Doe");
        existingPlayer.setGamesPlayed(1);

        Stats existingStats = new Stats(10, 5, 2, 1, 0, 2, 3, 30f); // ממוצע נוכחי
        PlayerSeasonStats existingPlayerStats = new PlayerSeasonStats();
        existingPlayerStats.setPlayerName("John Doe");
        existingPlayerStats.setSeason("2025");
        existingPlayerStats.setStats(existingStats);

        Stats newGameStats = new Stats(20, 7, 4, 3, 1, 1, 2, 35f); // נתונים של משחק חדש

        when(playerRepository.findByName("John Doe")).thenReturn(existingPlayer);
        when(playerSeasonStatsRepository.findByNameAndSeason("John Doe", "2025")).thenReturn(existingPlayerStats);
        when(teamSeasonStatsRepository.findByTeamAndSeason(Team.BOSTON_CELTICS, "2025")).thenReturn(null); // New team

        GameStatsMsg msg = new GameStatsMsg(Team.BOSTON_CELTICS, List.of(Pair.of("John Doe", newGameStats)));

        statsService.processGameStats(msg);


        ArgumentCaptor<PlayerSeasonStats> playerStatsCaptor = ArgumentCaptor.forClass(PlayerSeasonStats.class);
        verify(playerSeasonStatsRepository).save(playerStatsCaptor.capture());

        Stats updatedStats = playerStatsCaptor.getValue().getStats();

        assertEquals(15, updatedStats.getPoints());
        assertEquals(6, updatedStats.getRebounds());
        assertEquals(3, updatedStats.getAssists());
        assertEquals(2, updatedStats.getSteals());
        assertEquals(0, updatedStats.getBlocks());
        assertEquals(1, updatedStats.getTurnovers());
        assertEquals(2, updatedStats.getFouls());
        assertEquals(32.5f, updatedStats.getMinutesPlayed(), 0.01);
    }
}
