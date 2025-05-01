package kve.incoming.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Data
public class stats {
    int points;
    int rebounds;
    int assists;
    int steals;
    int blocks;
    int turnovers;
    @Min(0)
    @Max(6)
    int fouls;
    @DecimalMin("0.0")
    @DecimalMax("48.0")
    int minutesPlayed;


}
