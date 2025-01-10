package fr.fanto.fantodisbot.lol;

import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import fr.fanto.fantodisbot.lol.LoLGameTracker;
import fr.fanto.fantodisbot.lol.LoLEmbed;

public class LolMain extends ListenerAdapter {
    private final LoLGameTracker gameTracker;
    
    public LolMain(String riotApiKey, TextChannel outputChannel) {
        this.gameTracker = new LoLGameTracker(riotApiKey, outputChannel);
    }
    
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().split("\\s+");
        
        /*
        if (args[0].equals("!track")) {
            if (args.length > 1) {
                gameTracker.addPlayer(args[1]);
            }
        } else if (args[0].equals("!untrack")) {
            if (args.length > 1) {
                gameTracker.removePlayer(args[1]);
            }
        }
        */
        if (args[0].equals("!getpuuid")){
            if (args.length > 1) {
                String puuid = gameTracker.getPuuid(args[1]);
                event.getChannel().sendMessage("PUUID de " + args[1] + " : " + puuid).queue();
            }
        }
        if (args[0].equals("!embedtest")){
            event.getMessage().replyEmbeds(LoLEmbed.getMatchEmbed(event)).queue();
        }
    }
}