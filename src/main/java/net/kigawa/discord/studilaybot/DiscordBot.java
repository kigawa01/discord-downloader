package net.kigawa.discord.studilaybot;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.kigawa.log.LogSender;

import javax.security.auth.login.LoginException;

public class DiscordBot implements LogSender {
    public static String COMMAND_PREFIX = "st";
    private static DiscordBot discordBot;
    private final String ownerId;
    private final String token;
    private JDA jda;

    public DiscordBot(String ownerId, String token) {
        this.ownerId = ownerId;
        this.token = token;
        discordBot = this;

        info("log in discord bot now");
        CommandClient commandClient = new CommandClientBuilder()
                .setPrefix(COMMAND_PREFIX)
                .setOwnerId(this.ownerId)
                .build();
        try {
            jda = JDABuilder.createDefault(this.token)
                    .addEventListeners(commandClient)
                    .build();
        } catch (LoginException e) {
            warning(e);
        }
        onlineStatus(OnlineStatus.ONLINE);

        info("finished log in");
    }

    public static DiscordBot getInstance() {
        return discordBot;
    }

    public OnlineStatus onlineStatus(OnlineStatus onlineStatus) {
        if (onlineStatus == null) return jda.getPresence().getStatus();
        jda.getPresence().setStatus(onlineStatus);
        return onlineStatus;
    }

    public void disable() {
        info("log out discord bot now");
        onlineStatus(OnlineStatus.OFFLINE);

        jda.shutdown();
        info("finished log out");
    }
}
