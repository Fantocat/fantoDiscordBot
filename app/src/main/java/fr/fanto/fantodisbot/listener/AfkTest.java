package fr.fanto.fantodisbot.listener;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.JDA;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.lang.Long;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AfkTest extends ListenerAdapter {

    private final Map<Member, Instant> joinTimeMap = new HashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {

        if(event.getMember().getUser().isBot()) return;

        Member member = event.getMember();

        // Vérifie si l'utilisateur a rejoint un salon vocal
        if (event.getChannelJoined() instanceof VoiceChannel joinedChannel) {
            joinTimeMap.put(member, Instant.now());

            System.out.println(member.getEffectiveName() + " vient de rejoindre le salon " + joinedChannel.getName());

            // Planifie une tâche pour vérifier s'il reste mute après 5 minutes
            if (event.getMember().getUser().getId().equals("1283544287413534895")) {
                scheduler.schedule(() -> checkAndBan(member), 5, TimeUnit.MINUTES);
            } else {
                scheduler.schedule(() -> checkAndDisconect(member), 5, TimeUnit.MINUTES);
            }
        }

        // Supprime l'utilisateur de la liste s'il quitte le salon vocal
        if (event.getChannelLeft() instanceof VoiceChannel) {
            joinTimeMap.remove(member);
        }

        if (event.getMember() != null && 
        event.getChannelLeft() == null && 
        event.getChannelJoined() == null) {
        
        // Vérifie si l'utilisateur est mute
        boolean isSelfMuted = member.getVoiceState().isSelfMuted();
        
        if (isSelfMuted) {
            if (member != null && joinTimeMap.containsKey(member)) {
            Instant joinTime = joinTimeMap.get(member);
            long timeSinceJoin = Instant.now().getEpochSecond() - joinTime.getEpochSecond();

            // Si l'utilisateur se mute moins de 5 minutes après avoir rejoint
            if (timeSinceJoin <= 300) {
                if (event.getMember().getUser().getId().equals("1283544287413534895")) {
                    banUserForAfk(member, "Se mute rapidement après avoir rejoint un salon vocal");
                }
                
            }
        }
        } 
    }
    }

    private void checkAndBan(Member member) {
        if (member.getVoiceState() != null && member.getVoiceState().isMuted()) {
            banUserForAfk(member, "Reste sourdine plus de 5 minutes");
        }
    }

    private void checkAndDisconect(Member member) {
        if (member.getVoiceState() != null && member.getVoiceState().isMuted()) {
            member.getGuild().kickVoiceMember(member).queue();
        }
    }

    private void banUserForAfk(Member member, String reason) {
    if (member == null || !joinTimeMap.containsKey(member)) return;
    
    // Ban temporaire de 5 minutes
    member.ban(0, TimeUnit.SECONDS).reason(reason).queue(success -> {
        System.out.println("Banni temporairement " + member.getEffectiveName() + " pour la raison : " + reason);
        
        // Supprime l'utilisateur de la map après le ban
        joinTimeMap.remove(member);
        
        // Stocke l'ID de l'utilisateur pour l'unban
        String userId = member.getId();
        String userName = member.getEffectiveName();
        Guild guild = member.getGuild();
        
        // Planifie un débannissement après 5 minutes
        scheduler.schedule(() -> {
            // Crée un UserSnowflake à partir de l'ID stocké
            UserSnowflake userToUnban = User.fromId(userId);
            
            guild.unban(userToUnban).queue(
                unbanSuccess -> System.out.println("Utilisateur débanni : " + userName),
                error -> System.err.println("Erreur lors du unban de " + userName + ": " + error.getMessage())
            );
        }, 5, TimeUnit.MINUTES);
        
        // Envoie une invitation en MP
        sendPrivateMessage(member, "Vous avez été banni temporairement (5 minutes) pour la raison suivante : " + reason +
                ". Voici une invitation pour revenir : https://discord.gg/2khQEx7YJS");
    }, error -> System.err.println("Erreur en bannissant " + member.getEffectiveName() + ": " + error.getMessage()));
}

private void sendPrivateMessage(Member member, String message) {
    member.getUser().openPrivateChannel().queue(channel -> {
        channel.sendMessage(message).queue(
                success -> System.out.println("Message envoyé à " + member.getEffectiveName()),
                error -> System.err.println("Impossible d'envoyer un MP à " + member.getEffectiveName() + ": " + error.getMessage())
        );
    });
}
}
