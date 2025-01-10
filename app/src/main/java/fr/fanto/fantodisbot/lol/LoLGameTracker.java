package fr.fanto.fantodisbot.lol;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import java.util.*;
import java.util.concurrent.*;

import fr.fanto.fantodisbot.lol.LoLApiClient;

public class LoLGameTracker {
    private final LoLApiClient apiClient;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final TextChannel outputChannel;
    
    
    public LoLGameTracker(String apiKey, TextChannel channel) {
        this.apiClient = new LoLApiClient(apiKey);
        this.outputChannel = channel;
    }


    public String getPuuid(String riotName) {
        try {
            String summonerName = getSummonerName(riotName);
            String tagLine = getTagLine(riotName);
            return apiClient.getPuuid(summonerName, tagLine);
        } catch (Exception e) {
            return "Erreur lors de la récupération du puuid: " + e.getMessage();
        }
    }

    public String getSummonerName(String riotName) {
        return riotName.split("#")[0];
    }

    public String getTagLine(String riotName) {
        return riotName.split("#")[1];
    }

}