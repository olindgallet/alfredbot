package main.java.alfredbot.app.cronbot;

import main.java.alfredbot.app.cronbot.BotCommand;

/**
 * @author Olin Gallet
 * @date 3 Oct 2022
 * 
 * A <code>RunnableBotCommand</code> is a wrapper for <code>BotCommand</code>
 * to allow the command to be run in a thread and scheduled.
 */ 
public class RunnableBotCommand implements Runnable {
    private BotCommand botCommand;
    
    /**
     * Constructs a new <code>RunnableBotCommand</code>.
     * @param botCommand the bot command to make runnable
     */
    public RunnableBotCommand(BotCommand botCommand){
        this.botCommand = botCommand;
    }

    /**
     * Runs the command.
     */    
    public void run(){
        try {
            this.botCommand.perform();
        } catch (Exception e){
            System.out.println("Could not perform bot command: " + e.getMessage());
        }
    }
}
