package net.kigawa.discord.studilaybot;

import net.kigawa.app.Application;
import net.kigawa.app.Terminal;
import net.kigawa.file.FileUtil;
import net.kigawa.log.Logger;
import net.kigawa.thread.ThreadExecutors;

import java.util.logging.Level;

public class StudilayBot extends Application {
    private static final String TOKEN = "";
    private static final String OWNER_ID = "617576850654232608";
    private static final String COMMAND_PREFIX = "st";
    public static boolean J_LINE = true;
    public static StudilayBot studilayBot;

    public static void main(String[] args) {
        new Logger(StudilayBot.class.getName(), null, Level.INFO, FileUtil.getRelativeFile("logs")).enable();

        for (String s : args) {
            if (s.equals("deny-jline")) J_LINE = false;
        }

        studilayBot = new StudilayBot();
        studilayBot.enable();
    }

    @Override
    public void onEnable() {
        studilayBot.addModule(Logger.getInstance());
        studilayBot.enableAddModule(new ThreadExecutors());
        studilayBot.enableAddModule(new Terminal(J_LINE));

        info("enable studilay bot");
        Terminal.terminal.addOnRead(s -> {
            if (s.equals("stop")) disable();
        });

        while (isRun()) {
            synchronized (this) {
                try {
                    wait(1000);
                    info("wait");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public void onDisable() {
        info("disable studilay bot");

    }
}
