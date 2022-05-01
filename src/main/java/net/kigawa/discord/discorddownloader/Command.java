package net.kigawa.discord.discorddownloader;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.kigawa.kutil.log.log.Logger;

import java.util.concurrent.ExecutionException;

public class Command extends com.jagrosh.jdautilities.command.Command
{
    private final Logger logger = DiscordDownloader.getLogger();

    protected Command()
    {
        name = "download";
    }

    @Override
    protected void execute(CommandEvent commandEvent)
    {
        logger.info("on command");
        var channel = commandEvent.getChannel();

        DiscordDownloader.getExecutor().execute(() -> {
            var latestMessageId = channel.getLatestMessageIdLong();

            while (true) {
                MessageHistory history = null;
                try {
                    history = channel.getHistoryBefore(latestMessageId, 10).submit().get();
                } catch (InterruptedException | ExecutionException e) {
                    logger.warning(e);
                }

                if (history == null) return;
                var list = history.getRetrievedHistory();

                if (list.isEmpty()) return;

                for (Message message : list) {
                    logger.info(message.getContentRaw());
                }
                latestMessageId = list.get(list.size() - 1).getIdLong();
            }
        });
    }
}
