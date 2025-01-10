package fr.fanto.fantodisbot.listener;

import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;





public class PartyMode extends ListenerAdapter {
    private boolean partyMode = false;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private Guild currentGuild;
    private String myId = "359742389084225556";

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getMessage().getContentRaw().equals("!partyMode")) {
            if(!event.getAuthor().getId().equals(myId)) {
                event.getChannel().sendMessage("Un grand pouvoir implique de grande responsabilité.").queue();
                return;
            }
            partyMode = true;
            currentGuild = event.getGuild();
            startPartyMode();
            event.getChannel().sendMessage("🎉 Mode party activé ! 🎉").queue();
        }
        if (event.getMessage().getContentRaw().equals("!stopPartyMode")) {
            if(!event.getAuthor().getId().equals(myId)) {
                event.getChannel().sendMessage("Un grand pouvoir implique de grande responsabilité.").queue();
                return;
            }
            partyMode = false;
            stopPartyMode();
            event.getChannel().sendMessage("Mode party désactivé").queue();
        }
    }

    private void startPartyMode() {
        if (!partyMode) return;
        
        Random random = new Random();
        int delay = random.nextInt(20) + 1; // Délai entre 1 et 20 secondes

        scheduler.schedule(() -> {
            if (partyMode) {
                disconnectRandomMember();
                startPartyMode(); // Programme la prochaine déconnexion
            }
        }, delay, TimeUnit.SECONDS);
    }

    private void stopPartyMode() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }

    private void disconnectRandomMember() {
        if (currentGuild == null) return;

        List<Member> connectedMembers = new ArrayList<>();

        // Collecte tous les membres connectés en vocal
        for (VoiceChannel channel : currentGuild.getVoiceChannels()) {
            connectedMembers.addAll(channel.getMembers());
        }

        // Vérifie s'il y a des membres à déconnecter
        if (connectedMembers.isEmpty()) {
            System.out.println("Aucun membre connecté en vocal");
            return;
        }

        // Sélectionne un membre au hasard
        Random random = new Random();
        Member targetMember = connectedMembers.get(random.nextInt(connectedMembers.size()));

        // Déconnecte le membre
        currentGuild.kickVoiceMember(targetMember).queue(
            success -> System.out.println("🎉 Membre déconnecté : " + targetMember.getEffectiveName()),
            error -> System.out.println("❌ Erreur lors de la déconnexion : " + error.getMessage())
        );
    }
}