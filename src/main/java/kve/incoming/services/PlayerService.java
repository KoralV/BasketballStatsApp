package kve.incoming.services;

import kve.dto.Player;
import kve.incoming.repositories.PlayerRepository;
import org.springframework.stereotype.Service;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public Player savePlayer(Player player) {
        return playerRepository.save(player);
    }

    public Player getPlayerByName(String name) {
        return playerRepository.findByName(name);
    }
}