package kve.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class Stats {
    private int points;
    private int rebounds;
    private int assists;
    private int steals;
    private int blocks;
    private int turnovers;
    @Min(0)
    @Max(6)
    private int fouls;
    @DecimalMin("0.0")
    @DecimalMax("48.0")
    private float minutesPlayed;


}
