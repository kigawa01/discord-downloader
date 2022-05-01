package net.kigawa.discord.discorddownloader;

import net.kigawa.kutil.app.ApplicationBase;
import net.kigawa.kutil.app.Option;
import net.kigawa.kutil.kutil.KutilFile;
import net.kigawa.kutil.log.log.Logger;
import net.kigawa.kutil.terminal.Terminal;
import net.kigawa.kutil.thread.ThreadExecutor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class DiscordDownloader extends ApplicationBase
{
    public static final File DOWNLOAD_FILE = KutilFile.getRelativeFile("download");
    public static final String COMMAND_PREFIX = "";
    private static final String OWNER_ID = "617576850654232608";
    private static String TOKEN = "";
    private static DiscordDownloader discordDownloader;

    protected DiscordDownloader(String[] args)
    {
        super(args);
        discordDownloader = this;

        addModule(new DiscordBot(OWNER_ID));

        terminal.addOnRead(s -> {
            if (s != null && s.equals("stop")) disable();
        });
    }

    protected static String getTOKEN()
    {
        return TOKEN;
    }

    public static ThreadExecutor getExecutor()
    {
        return discordDownloader.executor;
    }

    public static DiscordDownloader getInstance()
    {
        return discordDownloader;
    }

    public static Logger getLogger()
    {
        return discordDownloader.logger;
    }

    public static void main(String[] args)
    {
        new DiscordDownloader(args);
        discordDownloader.enable();
    }

    @Override
    public void enable()
    {
        var tokenFile = KutilFile.getRelativeFile("token");
        try {
            tokenFile.createNewFile();
            var reader = new BufferedReader(new FileReader(tokenFile));
            TOKEN = reader.readLine();
            reader.close();

        } catch (IOException e) {
            logger.warning(e);
        }
        super.enable();
    }

    @Override
    public void onEnable()
    {
        logger.info("enable downloader");
        Terminal.terminal.addOnRead(s -> {
            if (s.equals("stop")) disable();
        });
    }

    @Override
    public void onDisable()
    {
        logger.info("disable studilay bot");

    }

    @Override
    protected void addAllOpt(List<Option> list)
    {
    }
}
