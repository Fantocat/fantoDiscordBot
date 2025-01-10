package fr.fanto.fantodisbot.listener;

import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Guild;

import java.util.Random;







public class ChatListener extends ListenerAdapter {

    private String myId = "359742389084225556";
    private String ZiakId = "1283544287413534895";

    private boolean partyMode = false;


    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot())
            return;
        if (event.getMessage().getContentRaw().equals("!ping"))
            event.getChannel().sendMessage("Pong!").queue();
        if (event.getMessage().getContentRaw().equals("Quoi"))
            event.getChannel().sendMessage("Feur!").queue();
            String authorId = event.getAuthor().getId();
        if (event.getMessage().getContentRaw().equals("!help")) {
            Random rand = new Random();
            int n = rand.nextInt(3);
            switch (n) {
                case 0:
                    event.getChannel().sendMessage("je suis pas ton objet je vais pas te dire comment m'utiliser!").queue();
                    break;
                case 1:
                    event.getChannel().sendMessage("Je suis rempli de surprise a vous de les découvrir !").queue();
                    break;
                case 2:
                    event.getChannel().sendMessage("Besoin d'aide? Demandez-moi!").queue();
                    event.getChannel().sendMessage("Non je vanne tu m'as faché").queue();
                    decoAuthorIfInVoiceChannel(event.getMember());
                    break;
            }    
        }
    }

    public void decoAuthorIfInVoiceChannel(Member member) {

        if (member.getVoiceState() != null && member.getVoiceState().inAudioChannel()) {
            member.getGuild().kickVoiceMember(member).queue(
                success -> System.out.println("Utilisateur déconnecté du salon vocal : " + member.getEffectiveName()),
                error -> System.err.println("Erreur lors de la tentative de déconnexion : " + error.getMessage())
            );
        } else {
            System.out.println("L'utilisateur n'est pas dans un salon vocal.");
        }
    }
    
    
}