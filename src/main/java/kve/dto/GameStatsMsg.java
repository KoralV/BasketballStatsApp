package kve.dto;

import lombok.Data;
import org.springframework.data.util.Pair;

import java.util.List;

@Data
public class GameStatsMsg {
    private Team teamName;
    private List<Pair<String, Stats>> playersStats;
}
