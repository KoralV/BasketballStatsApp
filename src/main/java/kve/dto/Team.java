package kve.dto;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Team {

    ATLANTA_HAWKS("Atlanta Hawks"),
    BOSTON_CELTICS("Boston Celtics"),
    BROOKLYN_NETS("Brooklyn Nets"),
    CHARLOTTE_HORNETS("Charlotte Hornets"),
    CHICAGO_BULLS("Chicago Bulls"),
    CLEVELAND_CAVALIERS("Cleveland Cavaliers"),
    DALLAS_MAVERICKS("Dallas Mavericks"),
    DENVER_NUGGETS("Denver Nuggets"),
    DETROIT_PISTONS("Detroit Pistons"),
    GOLDEN_STATE_WARRIORS("Golden State Warriors"),
    HOUSTON_ROCKETS("Houston Rockets"),
    INDIANA_PACERS("Indiana Pacers"),
    LOS_ANGELES_CLIPPERS("Los Angeles Clippers"),
    LOS_ANGELES_LAKERS("Los Angeles Lakers"),
    MEMPHIS_GRIZZLIES("Memphis Grizzlies"),
    MIAMI_HEAT("Miami Heat"),
    MILWAUKEE_BUCKS("Milwaukee Bucks"),
    MINNESOTA_TIMBERWOLVES("Minnesota Timberwolves"),
    NEW_ORLEANS_PELICANS("New Orleans Pelicans"),
    NEW_YORK_KNICKS("New York Knicks"),
    OKLAHOMA_CITY_THUNDER("Oklahoma City Thunder"),
    ORLANDO_MAGIC("Orlando Magic"),
    PHILADELPHIA_76ERS("Philadelphia 76ers"),
    PHOENIX_SUNS("Phoenix Suns"),
    PORTLAND_TRAIL_BLAZERS("Portland Trail Blazers"),
    SACRAMENTO_KINGS("Sacramento Kings"),
    SAN_ANTONIO_SPURS("San Antonio Spurs"),
    TORONTO_RAPTORS("Toronto Raptors"),
    UTAH_JAZZ("Utah Jazz"),
    WASHINGTON_WIZARDS("Washington Wizards");

    private final String displayName;

    Team(String displayName) {
        this.displayName = displayName;
    }

    public static Team fromName(String displayName) {
        return Arrays.stream(Team.values())
                .filter(team -> team.displayName.equalsIgnoreCase(displayName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid team name: " + displayName));
    }

    @Override
    public String toString() {
        return this.displayName;
    }

}
