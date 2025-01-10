package fr.fanto.fantodisbot.lol;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

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
        return response.get("puuid").getAsString();
    }
    
    private JsonObject makeApiCall(String url) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("X-Riot-Token", API_KEY)
                .build();
        
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200) {
            throw new Exception("API Error: " + response.statusCode() + " - " + response.body());
        }
        
        return gson.fromJson(response.body(), JsonObject.class);
    }
}