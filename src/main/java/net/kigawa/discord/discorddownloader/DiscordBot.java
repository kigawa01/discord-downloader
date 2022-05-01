package net.kigawa.discord.discorddownloader;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.kigawa.kutil.kutil.interfaces.Module;
import net.kigawa.kutil.log.log.Logger;

import javax.security.auth.login.LoginException;

public class DiscordBot implements Module
{
    private static DiscordBot discordBot;
    private final String ownerId;
    private final String token;
    private final Logger logger = DiscordDownloader.getLogger();
    private JDA jda;

    public DiscordBot(String ownerId, String token)
    {
        this.ownerId = ownerId;
        this.token = token;
        discordBot = this;
    }

    public static DiscordBot getInstance()
    {
        return discordBot;
    }

    public OnlineStatus onlineStatus(OnlineStatus onlineStatus)
    {
        if (onlineStatus == null) return jda.getPresence().getStatus();
        jda.getPresence().setStatus(onlineStatus);
        return onlineStatus;
    }

    @Override
    public void enable()
    {
        logger.info("log in discord bot now");
        CommandClient commandClient = new CommandClientBuilder()
                .setPrefix(DiscordDownloader.COMMAND_PREFIX)
                .setOwnerId(this.ownerId)
                .addCommand(new Command())
                .build();
        try {
            jda = JDABuilder.createDefault(this.token)
                    .addEventListeners(commandClient)
                    .build();
        } catch (LoginException e) {
            logger.warning(e);
        }
        onlineStatus(OnlineStatus.ONLINE);
        logger.info("finished log in");
    }

    public void disable()
    {
        logger.info("log out discord bot now");
        onlineStatus(OnlineStatus.OFFLINE);

        jda.shutdown();
        logger.info("finished log out");
    }
}
