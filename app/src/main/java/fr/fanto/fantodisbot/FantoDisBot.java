package fr.fanto.fantodisbot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import java.sql.Connection;

import fr.fanto.fantodisbot.listener.ChatListener;
import fr.fanto.fantodisbot.listener.FunnyDisconnect;
import fr.fanto.fantodisbot.listener.AfkTest;
import fr.fanto.fantodisbot.listener.PartyMode;
import fr.fanto.fantodisbot.lol.LolMain;
import fr.fanto.fantodisbot.config.SQLiteConnection;
import fr.fanto.fantodisbot.repositories.PlayerRepo;



public class FantoDisBot {

    public static void main(String[] args) throws InterruptedException 
    {

        String riotApiKey = "RGAPI-424a2eec-4e38-4b6e-81cb-3ec763174b9c";
        Connection conn = SQLiteConnection.connect();

        PlayerRepo playerRepo = new PlayerRepo(conn);
        playerRepo.createTable();
        

        JDA jda = JDABuilder.createDefault("MTE2MTIzOTAxOTg3MTU0NzQ3Mg.G28uxa.pZrRE4xqUfB4t4Y5chdcr-5ozQgUwml3YjIG8E")
            .addEventListeners(new ChatListener())
            .addEventListeners(new FunnyDisconnect())
            .addEventListeners(new AfkTest())
            .addEventListeners(new PartyMode())
            .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
            .build();

        jda.awaitReady();

        

        GuildChannel channel = jda.getTextChannelById("1326944913962832016");
        TextChannel outputChannel = null;
        if (channel instanceof TextChannel) {
            outputChannel = (TextChannel) channel;
        }

        jda.addEventListener(new LolMain(riotApiKey, outputChannel, conn));
    }
}
