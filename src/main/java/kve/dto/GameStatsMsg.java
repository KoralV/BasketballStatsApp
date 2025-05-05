package kve.dto;

import lombok.Data;

import java.util.List;

@Data
public class GameStatsMsg {
    private Team teamName;
    private List<Player> players;


}
