package kve.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Player {

    @NotBlank
    private String name;
    @Valid
    @NotEmpty
    private Stats stats;
    @NotNull
    private Team team;
}
