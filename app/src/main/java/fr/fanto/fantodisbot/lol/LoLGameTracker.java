package fr.fanto.fantodisbot.lol;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import java.util.*;
import java.util.concurrent.*;
import java.sql.Connection;

import fr.fanto.fantodisbot.lol.LoLApiClient;
import fr.fanto.fantodisbot.entities.Player;
import fr.fanto.fantodisbot.repositories.PlayerRepo;

public class LoLGameTracker {

    private final LoLApiClient apiClient;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final TextChannel outputChannel;
    private final List<String> playersUUID = new ArrayList<>();
    private final PlayerRepo playerRepo;
    private final String lolChannelId = "1326944913962832016";

    
    
    public LoLGameTracker(String apiKey, TextChannel channel, Connection conn) {
        this.apiClient = new LoLApiClient(apiKey);
        this.outputChannel = channel;
        this.playerRepo = new PlayerRepo(conn);
        setPlayersUUID();
    }

    public void setPlayersUUID() {
        List<Player> players = playerRepo.getPlayers();
        for (Player player : players) {
            playersUUID.add(player.getPuuid());
        }
    }

    public String getPuuid(String riotName) {
        try {
            String summonerName = getSummonerName(riotName);
            String tagLine = getTagLine(riotName);
            String puuid = apiClient.getPuuid(summonerName, tagLine);
            if (puuid == null) {
                return null;
            } else {
                return puuid;
            }
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

    public void addPlayer(String riotName) {
        try {
            String puuid = getPuuid(riotName);
            if (puuid == null) {
                outputChannel.sendMessage("Joueur introuvable : " + riotName).queue();
                return;
            } else
            if (playersUUID.contains(puuid)) {
                outputChannel.sendMessage("Joueur déjà suivi : " + riotName).queue();
                return;
            } else {
                Player player = new Player(riotName, getSummonerName(riotName), getTagLine(riotName), puuid);
                System.out.println(puuid);
                playerRepo.insertPlayer(player);
                playersUUID.add(puuid);
                outputChannel.sendMessage("Joueur ajouté à la liste de suivi : " + riotName).queue();
            }
        } catch (Exception e) {
            outputChannel.sendMessage("Erreur lors de l'ajout du joueur : " + e.getMessage()).queue();
        }
    }

    public void deletePlayer(String riotName) {
        try {
            String puuid = getPuuid(riotName);
            if (playersUUID.contains(puuid)) {
                playerRepo.deletePlayer(riotName);
                playersUUID.remove(puuid);
                outputChannel.sendMessage("Joueur retiré de la liste de suivi : " + riotName).queue();
            } else {
                outputChannel.sendMessage("Joueur non suivi : " + riotName).queue();
            }
        } catch (Exception e) {
            outputChannel.sendMessage("Erreur lors de la suppression du joueur : " + e.getMessage()).queue();
        }
    }

    public void listPlayers() {
        List<Player> players = playerRepo.getPlayers();
        if (players.isEmpty()) {
            outputChannel.sendMessage("Aucun joueur suivi").queue();
        } else {
            StringBuilder message = new StringBuilder();
            message.append("Liste des joueurs suivis : \n");
            for (Player player : players) {
                message.append(player.getRiotName()).append("\n");
            }
            outputChannel.sendMessage(message.toString()).queue();
        }
    }
    /*
    public List<String> getLastMatchs(String puuid, int count) {
        try {
            JsonArray matchs = apiClient.getMatchs(puuid, count);
            if (matchs == null) {
                return null;
            } else {
                return matchs;
            }
        } catch (Exception e) {
            return "Erreur lors de la récupération des matchs: " + e.getMessage();
        }
    }

    public boolean checkLastMatchs(String puuid,String matchId) {
        Player player = playerRepo.getPlayerByPuuid(puuid);
        String lastGameSave = player.getLastGame();
        if (lastGameSave.equals(matchId)) {
            return false;
        } else {
            player.setLastGame(matchId);
            playerRepo.updatePlayer(player);
            return true;
        }
    }
    */

    public static void extractPlayerInfo(JsonObject matchData, String playerPuuid) {
        JsonObject info = matchData.getAsJsonObject("info");
        JsonArray participants = info.getAsJsonArray("participants");
        
        // Trouver le joueur à partir du PUUID
        JsonObject player = null;
        for (int i = 0; i < participants.size(); i++) {
            JsonObject participant = participants.get(i).getAsJsonObject();
            if (participant.get("puuid").getAsString().equals(playerPuuid)) {
                player = participant;
                break;
            }
        }

        if (player == null) {
            System.out.println("Player not found in match.");
            return;
        }

        // Récupérer l'ID de l'équipe du joueur
        int teamId = player.get("teamId").getAsInt();

        // Extraire les informations du joueur
        System.out.println("Game Start: " + info.get("gameStartTimestamp").getAsLong());
        System.out.println("Did Win: " + player.get("win").getAsBoolean());
        System.out.println("Champion Name: " + player.get("championName").getAsString());
        System.out.println("Champion ID: " + player.get("championId").getAsInt());
        System.out.println("First Blood Kill: " + player.get("firstBloodKill").getAsBoolean());
        System.out.println("First Blood Assist: " + player.get("firstBloodAssist").getAsBoolean());
        System.out.println("First Tower Kill: " + player.get("firstTowerKill").getAsBoolean());
        System.out.println("First Tower Assist: " + player.get("firstTowerAssist").getAsBoolean());
        System.out.println("Boots Upgraded: " + isBootsUpgraded(player));
        System.out.println("Vision Score: " + player.get("visionScore").getAsInt());
        System.out.println("Bought Pink Ward: " + hasBoughtPinkWard(player));
        System.out.println("K/D/A: " + player.get("kills").getAsInt() + "/" +
                player.get("deaths").getAsInt() + "/" + player.get("assists").getAsInt());
        System.out.println("Queue Type: " + determineQueueType(info.get("queueId").getAsInt()));

        // Vérifier si l'équipe a débloqué des bottes améliorées
        boolean teamHasBoots = teamHasUpgradedBoots(participants, teamId);
        System.out.println("Team Has Upgraded Boots: " + teamHasBoots);
    }

    // Méthode pour vérifier si les bottes sont améliorées
    private static boolean isBootsUpgraded(JsonObject player) {
        int[] items = {
            player.get("item0").getAsInt(),
            player.get("item1").getAsInt(),
            player.get("item2").getAsInt(),
            player.get("item3").getAsInt(),
            player.get("item4").getAsInt(),
            player.get("item5").getAsInt(),
            player.get("item6").getAsInt()
        };

        // Liste des ID des bottes améliorées
        int[] upgradedBoots = {3175, 3174, 3172, 3170, 3173, 3176, 3171};
        for (int item : items) {
            for (int boot : upgradedBoots) {
                if (item == boot) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean hasBoughtPinkWard(JsonObject player) {
        // Vérifie si des balises de contrôle (Control Ward) ont été placées
        return player.get("visionWardsBoughtInGame").getAsInt() > 0;
    }

    // Méthode pour vérifier si l'équipe a des bottes améliorées
    private static boolean teamHasUpgradedBoots(JsonArray participants, int teamId) {
        for (int i = 0; i < participants.size(); i++) {
            JsonObject participant = participants.get(i).getAsJsonObject();
            if (participant.get("teamId").getAsInt() == teamId) {
                if (isBootsUpgraded(participant)) {
                    return true; // Si un joueur de l'équipe a des bottes améliorées
                }
            }
        }
        return false;
    }

    // Méthode pour déterminer le type de queue
    private static String determineQueueType(int queueId) {
        switch (queueId) {
            case 400:
                return "Draft Pick";
            case 420:
                return "Ranked Solo/Duo";
            case 430:
                return "Blind Pick";
            case 440:
                return "Ranked Flex";
            case 450:
                return "ARAM";
            case 700:
                return "Clash";
            case 900:
                return "URF";
            case 1020:
                return "One for All";
            default:
                return "Special Mode";
        }
    }

    public void testMatchData(String matchId) {
        try {
            JsonObject matchData = apiClient.getMatchData(matchId);
            if (matchData == null) {
                outputChannel.sendMessage("Match introuvable : " + matchId).queue();
            } else {
                extractPlayerInfo(matchData, "kzs7-psmRR1cas6WmVbSiJNTiJrxs7FfnRQLMMWl5tvqTFuojTBZPWLVV4IvjMakBbvzoTn-JKmi1w");
            }
        } catch (Exception e) {
            outputChannel.sendMessage("Erreur lors de la récupération des données du match : " + e.getMessage()).queue();
        }
    }
}
