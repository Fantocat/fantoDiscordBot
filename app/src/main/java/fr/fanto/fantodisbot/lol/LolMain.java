package fr.fanto.fantodisbot.lol;

import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import java.sql.Connection;

import fr.fanto.fantodisbot.lol.LoLGameTracker;
import fr.fanto.fantodisbot.lol.LoLEmbed;

public class LolMain extends ListenerAdapter {
    private final LoLGameTracker gameTracker;
    private final String lolChannelId = "1326944913962832016";
    
    public LolMain(String riotApiKey, TextChannel outputChannel, Connection conn) {
        this.gameTracker = new LoLGameTracker(riotApiKey, outputChannel, conn);
    }
    
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().split("\\s+");

        if (!event.getChannel().getId().equals(lolChannelId)) {
            return;
        }
        
        switch (args[0]) {

            case "!track" -> {
                if (args.length > 1) {
                    gameTracker.addPlayer(args[1]);
                } else {
                    event.getChannel().sendMessage("Veuillez renseigner un pseudo : Exemple#EXE").queue();
                }
            }

            case "!untrack" -> {
                if (args.length > 1) {
                    gameTracker.deletePlayer(args[1]);
                } else {
                    event.getChannel().sendMessage("Veuillez renseigner un pseudo : Exemple#EXE").queue();
                }
            }
    
            case "!list" -> {
                gameTracker.listPlayers();
            }

            case "!testMatchData" -> {
                gameTracker.testMatchData(args[1]);
            }
        }
    }
}