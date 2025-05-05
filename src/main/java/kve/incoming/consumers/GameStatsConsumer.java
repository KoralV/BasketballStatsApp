package kve.incoming.consumers;

import kve.dto.GameStatsMsg;
import kve.incoming.services.PlayerService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class GameStatsConsumer {

    private final PlayerService playerService;

    public GameStatsConsumer(PlayerService playerService) {
        this.playerService = playerService;
    }

    @KafkaListener(topics = "game-stats", groupId = "basketball-group", containerFactory = "kafkaListenerContainerFactory")
    public void consume(GameStatsMsg gameStatsMsg) {
        playerService.processGameStats(gameStatsMsg);
    }
}
