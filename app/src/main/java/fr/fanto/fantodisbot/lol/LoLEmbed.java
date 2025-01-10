package fr.fanto.fantodisbot.lol;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.entities.MessageEmbed;


public class LoLEmbed {

    public static MessageEmbed getMatchEmbed(MessageReceivedEvent event){
        EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("Victoire"); // Titre
            embed.setAuthor("Annihilater");
            embed.setThumbnail("https://raw.communitydragon.org/latest/plugins/rcp-be-lol-game-data/global/default/v1/champion-icons/1.png");
            embed.setDescription("T nuuuuuull"); // Description
            embed.addField("First death", "1min47", true);
            embed.addField("Champion", "Aatrox", true);
            embed.addField("KDA", "18/0/3", true);

            embed.addField("Date", "09/01/2025 22h11", false); // Commentaire

            return embed.build();
    }
}