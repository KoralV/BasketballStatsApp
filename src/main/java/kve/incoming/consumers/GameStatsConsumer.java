package kve.incoming.consumers;

import kve.dto.GameStatsMsg;
import kve.incoming.services.StatsService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class GameStatsConsumer {

    private final StatsService statsService;

    public GameStatsConsumer(StatsService statsService) {
        this.statsService = statsService;
    }

    @KafkaListener(topics = "game-stats", groupId = "basketball-group", containerFactory = "kafkaListenerContainerFactory")
    public void consume(GameStatsMsg gameStatsMsg) {
        statsService.processGameStats(gameStatsMsg);
    }
}
