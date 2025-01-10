package fr.fanto.fantodisbot.listener;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.GuildVoiceState;



import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FunnyDisconnect extends ListenerAdapter {

    private final HashMap<String, Boolean> prankTargets = new HashMap<>(); // Pour stocker les personnes à déconnecter

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        String myId = "359742389084225556";

        

        String[] args = event.getMessage().getContentRaw().split(" ");

        // Commande pour activer la déconnexion aléatoire
        if (args[0].equalsIgnoreCase("!limitTest")) {
            if (!event.getAuthor().getId().equals(myId)) 
                event.getChannel().sendMessage("Un grand pouvoir implique de grande responsabilité.").queue();
            if (args.length < 2 || event.getMessage().getMentions().getMembers().isEmpty()) {
                event.getChannel().sendMessage("Usage : !limitTest @user").queue();
                return;
            }

            Member target = event.getMessage().getMentions().getMembers().get(0);
            prankTargets.put(target.getId(), true); // Active la "blague" pour cet utilisateur
            event.getChannel().sendMessage("La déconnexion aléatoire est activée pour " + target.getEffectiveName()).queue();
            startLimitTestIfAlreadyInVoiceChannel(event.getGuild());
        }

        // Commande pour désactiver la déconnexion aléatoire
        if (args[0].equalsIgnoreCase("!stopLimitTest")) {
            if (!event.getAuthor().getId().equals(myId)) 
                event.getChannel().sendMessage("Un grand pouvoir implique de grande responsabilité.").queue();
            if (args.length < 2 || event.getMessage().getMentions().getMembers().isEmpty()) {
                event.getChannel().sendMessage("Usage : !stopLimitTest @user").queue();
                return;
            }

            Member target = event.getMessage().getMentions().getMembers().get(0);
            prankTargets.remove(target.getId()); // Désactive la blague
            event.getChannel().sendMessage("La déconnexion aléatoire est désactivée pour " + target.getEffectiveName()).queue();
        }
    }

     @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
        // Vérifie si un utilisateur rejoint un salon vocal
        AudioChannelUnion joinedChannel = event.getChannelJoined();
        Member member = event.getMember();

        if (prankTargets.containsKey(member.getId())) {
            Random random = new Random();
            int delay = (1 + random.nextInt(20)) * 1000; // Délai aléatoire entre 1 et 20 secondes

            // Utilise un Timer pour exécuter la déconnexion après le délai
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    if (joinedChannel.getMembers().contains(member)) { // Vérifie si l'utilisateur est toujours dans le salon
                        event.getGuild().kickVoiceMember(member).queue(
                                success -> System.out.println(member.getEffectiveName() + " a été déconnecté après " + (delay / 1000) + " secondes."),
                                failure -> System.out.println("Impossible de déconnecter " + member.getEffectiveName())
                        );
                    }
                }
            }, delay);
        }
    }

    public void startLimitTestIfAlreadyInVoiceChannel(Guild guild) {
    System.out.println("Début de la vérification des salons vocaux...");
    System.out.println("Nombre de cibles dans prankTargets: " + prankTargets.size());
    
    // Log des cibles actuelles
    prankTargets.forEach((id, count) -> 
        System.out.println("Cible enregistrée: ID=" + id)
    );

    for (VoiceChannel voiceChannel : guild.getVoiceChannels()) {
        System.out.println("Vérification du salon: " + voiceChannel.getName() + " (ID: " + voiceChannel.getId() + ")");
        
        List<Member> members = voiceChannel.getMembers();
        System.out.println("Nombre de membres dans ce salon: " + members.size());
        
        for (Member member : members) {
            System.out.println("Vérification du membre: " + member.getEffectiveName() + " (ID: " + member.getId() + ")");
            
            if (prankTargets.containsKey(member.getId())) {
                System.out.println("Cible trouvée: " + member.getEffectiveName());
                
                Random random = new Random();
                int delay = (1 + random.nextInt(20)) * 1000;
                System.out.println("Délai programmé pour " + member.getEffectiveName() + ": " + delay + "ms");
                
                // Utilisation de ScheduledExecutorService au lieu de Timer
                ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
                scheduler.schedule(() -> {
                    try {
                        System.out.println("Tentative de déconnexion de " + member.getEffectiveName() + "...");
                        
                        // Récupération du membre à jour
                        Member updatedMember = guild.getMemberById(member.getId());
                        if (updatedMember == null) {
                            System.out.println("Membre non trouvé dans la guilde: " + member.getEffectiveName());
                            return;
                        }
                        
                        // Vérification si le membre est toujours dans un salon vocal
                        GuildVoiceState voiceState = updatedMember.getVoiceState();
                        if (voiceState != null && voiceState.getChannel() != null) {
                            guild.kickVoiceMember(updatedMember).queue(
                                success -> {
                                    System.out.println(updatedMember.getEffectiveName() + " a été déconnecté après " + (delay / 1000) + " secondes.");
                                    // Fermeture du scheduler après utilisation
                                    scheduler.shutdown();
                                },
                                failure -> {
                                    System.out.println("Impossible de déconnecter " + updatedMember.getEffectiveName() + ": " + failure.getMessage());
                                    scheduler.shutdown();
                                }
                            );
                        } else {
                            System.out.println(updatedMember.getEffectiveName() + " n'est plus dans un salon vocal");
                            scheduler.shutdown();
                        }
                    } catch (Exception e) {
                        System.out.println("Erreur lors de la déconnexion: " + e.getMessage());
                        e.printStackTrace();
                        scheduler.shutdown();
                    }
                }, delay, TimeUnit.MILLISECONDS);
            } else {
                System.out.println("Membre non ciblé: " + member.getEffectiveName());
            }
        }
    }
}
}

