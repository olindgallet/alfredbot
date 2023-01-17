package main.java.alfredbot.app.cronbot;

import main.java.alfredbot.app.cronbot.BotCommand;
import java.util.NoSuchElementException;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.mention.AllowedMentions;
import org.javacord.api.entity.message.mention.AllowedMentionsBuilder;
import org.javacord.api.entity.user.User;

/**
 * @author Olin Gallet
 * @date 3 10 2022
 * 
 * An <code>AbstractBotCommand</code> implements information about a <code>BotCommand</code>
 * but leaves the <code>perform()</code> function for subclasses.
 */
public abstract class AbstractBotCommand implements BotCommand{
    private DiscordApi api;
    private String name;
    private String jobData;
    private String cronString;
    private String cronDescription;
    private User[] notifiedUsers;
    private TextChannel[] targetChannels;

    /**
     * Constructs a new <code>AbstractBotCommand</code>.
     * @param api             a connected Discord API for this to use
     * @param name            a unique name for this bot command
     * @param jobData         information needed for the bot do its job
     * @param cronString      cron information to determine its schedule
     * @param cronDescription a human readable interpretation of the cron timing
     * @param notifiedUsers   an array of Discord users IDs to notify on completion of this command
     * @param targetChannels  an array of Discord channel IDs to message with this command
     */
    public AbstractBotCommand(DiscordApi api, String name, String jobData, String cronString, String cronDescription, User[] notifiedUsers, TextChannel[] targetChannels){
        this.api = api;
        this.name = name;
        this.jobData = jobData;
        this.cronString = cronString;
        this.cronDescription = cronDescription;
        this.notifiedUsers = notifiedUsers;
        this.targetChannels = targetChannels;
    }

    /**
     * Perform the given command.
     * Implemented by all subclasses.
     */
    public abstract void perform() throws Exception;

    /**
     * Return a unique name for this bot command.
     * @return a unique name for this bot command
     */
    public final String getName(){
        return this.name;
    }

    /**
     * Returns a cron formatted description of the execution timing of this command.
     * @return a cron formatted description of the execution timing of this command 
     */
    public final String getCronString(){
        return this.cronString;
    }

    /**
     * Returns a human readable description of the cron timing of this command.
     * @return a human readable description of the cron timing of this command
     */
    public final String getCronDescription(){
        return this.cronDescription;
    }

    protected final AllowedMentions getAllowedMentions(){
        AllowedMentionsBuilder builder = new AllowedMentionsBuilder();  
        for (int i = 0; i < this.notifiedUsers.length; i++){
            builder.addUser(this.notifiedUsers[i].getId());
        }
        return builder.build();
    }

    protected final TextChannel[] getTextChannels(){
        return this.targetChannels;
    }

    protected final User[] getNotifiedUsers(){
        return this.notifiedUsers;
    }

    protected final String getJobData(){
        return this.jobData;
    }

    /**
     * Returns a debug message.
     * @return String a debug message
     */
    public String toString(){
        return "Name: " + this.name + "\n" +
        "Job Data: " + this.jobData + "\n" +
        "Cron String: " + this.cronString + "\n" +
        "Cron Description: " + this.cronDescription;
    }
}
