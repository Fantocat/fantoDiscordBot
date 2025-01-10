package fr.fanto.fantodisbot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;

import fr.fanto.fantodisbot.listener.ChatListener;
import fr.fanto.fantodisbot.listener.FunnyDisconnect;
import fr.fanto.fantodisbot.listener.AfkTest;
import fr.fanto.fantodisbot.listener.PartyMode;
import fr.fanto.fantodisbot.lol.LolMain;



public class FantoDisBot {

    public static void main(String[] args) throws InterruptedException 
    {

        String riotApiKey = "RGAPI-06db2013-c440-44ea-845c-b16c820d203a";
        

        JDA jda = JDABuilder.createDefault("MTE2MTIzOTAxOTg3MTU0NzQ3Mg.G8skt6.iZzfNkEw17e5S4mFqzqxTvhqJvujycmp8KozF8")
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

        jda.addEventListener(new LolMain(riotApiKey, outputChannel));
    }
}
