package main.java.alfredbot.app.cronbot;

import main.java.alfredbot.app.cronbot.BotCommand;
import main.java.alfredbot.app.cronbot.RunnableBotCommand;
import cn.hutool.cron.Scheduler;

/**
 * @author Olin Gallet
 * @date 3 Oct 2022
 * 
 * The <code>CommandScheduler</code> takes in bot commands and 
 * schedules their execution based upon their provided cron strings.
 */
public class CommandScheduler{
    private static final CommandScheduler instance = new CommandScheduler();
    private BotCommand[] botCommands;
    private Scheduler commandScheduler;
    
    private CommandScheduler(){
        this.commandScheduler = new Scheduler();
    }

    /**
     * Clears the command list and sets the schedulers commands.
     * @param botCommands a list of bot commands to schedule
     */
    public void setCommands(BotCommand[] botCommands){
        this.commandScheduler.clear();
        this.botCommands = botCommands;

        for(int i = 0; i < botCommands.length; i++){
            this.commandScheduler.schedule(botCommands[i].getName(), botCommands[i].getCronString(), new RunnableBotCommand(botCommands[i]));
        }
    }

    /**
     * Clears the scheduler.
     */
    public void clear(){
        this.commandScheduler.clear();
    }

    /**
     * Starts the scheduler.
     */
    public void start(){
        this.commandScheduler.start();
    }

    /**
     * Stops the scheduler.
     */
    public void stop(){
        this.commandScheduler.stop();
    }

    /**
     * Creates a singleton instance of the scheduler.
     * @return a singleton instance of the scheduler
     */
    public static CommandScheduler getInstance(){
        return instance;
    }
}
