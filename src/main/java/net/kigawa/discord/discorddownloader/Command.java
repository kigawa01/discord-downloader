package net.kigawa.discord.discorddownloader;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.kigawa.kutil.kutil.KutilFile;
import net.kigawa.kutil.log.log.Logger;

import java.io.File;
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

                if (list.isEmpty()) {
                    logger.info("end download");
                    return;
                }

                for (Message message : list) {
                    download(message);
                }
                latestMessageId = list.get(list.size() - 1).getIdLong();
            }
        });
    }

    private void download(Message message)
    {
        DiscordDownloader.getExecutor().execute(() -> {
            var time = message.getTimeCreated();
            File channelDir = KutilFile.getFile(DiscordDownloader.DOWNLOAD_FILE,
                    replaceName(message.getChannel().getName())
            );
            File dir = KutilFile.getFile(channelDir,
                    time.getYear() + "-"
                            + time.getMonth()
                            + "-" + time.getDayOfMonth()
            );
            var attachments = message.getAttachments();

            if (attachments.isEmpty()) return;
            if (!dir.exists()) {
                logger.info("create dir " + dir.getName());
                dir.mkdirs();
            }

            for (Message.Attachment attachment : attachments) {
                logger.info("download " + attachment.getFileName());
                attachment.downloadToFile(KutilFile.getFile(dir,
                        replaceName(message.getAuthor().getName()) + "-"
                                + attachment.getFileName()));
            }
        });
    }

    private String replaceName(String str)
    {
        var result = str
                .replace('/', '_')
                .replace('\\', '_');
        return result;
    }
}
