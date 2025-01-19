package fr.fanto.fantodisbot.lol;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.util.List;

public class LoLApiClient {
    private final String API_KEY;
    private final HttpClient client;
    private final Gson gson;
    
    public LoLApiClient(String apiKey) {
        this.API_KEY = apiKey;
        this.client = HttpClient.newHttpClient();
        this.gson = new Gson();
    }
    
    // Obtenir l'ID du compte à partir du nom d'invocateur
    public String getPuuid(String summonerName, String tagLine) throws Exception {
        String url = String.format("https://europe.api.riotgames.com/riot/account/v1/accounts/by-riot-id/%s/%s",
                summonerName, tagLine);
        
        JsonObject response = makeApiCall(url);
        if (response == null) {
            return null;
        }
        return response.get("puuid").getAsString(); 
    }

    public List<String> getMatchs(String puuid, int count) throws Exception {
        String url = String.format("https://europe.api.riotgames.com/lol/match/v5/matches/by-puuid/%s/ids?start=0&count=%d",
                puuid, count);
        
        JsonObject response = makeApiCall(url);
        if (response == null) {
            return null;
        }
        return gson.fromJson(response.get("matchIds"), List.class);
    }

    public JsonObject getMatchData(String matchId) throws Exception {
        String url = String.format("https://europe.api.riotgames.com/lol/match/v5/matches/%s", matchId);
        
        return makeApiCall(url);
    }
    
    private JsonObject makeApiCall(String url) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("X-Riot-Token", API_KEY)
                .build();
        
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200) {
            return null;
        }
        
        return gson.fromJson(response.body(), JsonObject.class);
    }
}