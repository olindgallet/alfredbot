package main.java.alfredbot.app;

import org.javacord.api.DiscordApi;
import main.java.alfredbot.app.settings.SettingsLoader;
import main.java.alfredbot.app.cronbot.CommandScheduler;
import main.java.alfredbot.app.cronbot.BotCommand;

/**
 * @author Olin Gallet
 * @date 7 Oct 2022
 * 
 * <code>AlfredBot</code> is a Discord bot used for automated tasks.  It relies on a <code>bot-settings.json</code>
 * file for its tasks and timing.
 */
public class AlfredBot{
    private static final AlfredBot instance = new AlfredBot();

    private AlfredBot(){
    }

    public final static AlfredBot getInstance(){
        return instance;
    }

    /**
     * Starts the bot.
     * @throws Exception if something goes wrong to the point the bot needs to stop
     */
    public void start() throws Exception{
        SettingsLoader sl = SettingsLoader.getInstance();
		sl.load();
		CommandScheduler s = CommandScheduler.getInstance();
		DiscordApi api = sl.getApiBuilder().login().join();
        System.out.println("===========================================");
        System.out.println("Alfred Bot currently loaded.");
        System.out.println("API link at: " + api.createBotInvite());
        System.out.println("===========================================");
        BotCommand[] commands = sl.getBotCommands(api);
        
        for (int i = 0;i< commands.length; i++){
            System.out.println("Command: " + commands[i].getName() + " | " + commands[i].getCronDescription());
        }

		s.setCommands(commands);
		System.out.println("Starting bot. . .");
        s.start();
    }
}