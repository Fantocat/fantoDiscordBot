package fr.fanto.fantodisbot.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.Builder;

import jakarta.persistence.*;

@Getter
@Setter
@Builder
@Entity
public class Player {

    @Id
    private String riotName;
    private String gameName;
    private String tagLine;
    private String puuid;
    private String lastGameId;
    private int strickWin;
    private int strickLose;
    private String summonerId;

    // Stats
    // Weekly
    private int totalWeekTimePlayed;
    private int totalWeekWin;
    private int totalWeekLose;
    private int totalWeekTimeLoose;
    private int totalWeekTimeWin;
    private int totalWeekKills;
    private int totalWeekDeaths;
    private int weekRatio;
    @ManyToMany
    private Map<String, Integer> weekChampions;

    // Total
    private Long totalTimePlayed;
    private int totalTimeLose;
    private int totalTimeWin;
    private int totalGameLose;
    private int totalGameWin;
    private int totalGamePlayed;
    private int totalKills;
    private int totalDeaths;
    private int totalRatio;
    @ManyToMany
    private Map<String, Integer> totalChampions;


    public Player(String riotName, String gameName, String tagLine, String puuid) {
        this.riotName = riotName;
        this.gameName = gameName;
        this.tagLine = tagLine;
        this.puuid = puuid;
        this.strickWin = 0;
        this.strickLose = 0;
    }

}