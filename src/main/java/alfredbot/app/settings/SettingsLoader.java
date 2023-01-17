package main.java.alfredbot.app.settings;

import java.io.IOException;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.lang.InterruptedException;
import java.lang.NumberFormatException;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.entity.channel.TextChannel;
import main.java.alfredbot.app.cronbot.BotCommand;
import main.java.alfredbot.app.cronbot.FileTransferToDiscordCommand;
import com.cronutils.parser.CronParser;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.descriptor.CronDescriptor;
import com.cronutils.model.CronType;
import com.cronutils.model.Cron;

/**
 * @author Olin Gallet
 * @date 29 September 2022
 * 
 * The <code>SettingsLoader</code> loads configuration settings for <code>AlfredBot</code> from 
 * bot-settings.json in the same folder.  
 * 
 * The bot-settings.json provides:
 *   -API token to interact with Discord
 *   -Notifying events [name of event, target channels, and notified users]  
 */

public class SettingsLoader{
    private static final SettingsLoader instance = new SettingsLoader();
    private final CronParser parser = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.UNIX));
    private final CronDescriptor descriptor = CronDescriptor.instance(CronDescriptor.DEFAULT_LOCALE);
    
    private JSONObject settingsObject;
	
    private SettingsLoader(){
        this.settingsObject = null;
    }

    /**
     * Returns a singleton instance of a <code>SettingsLoader</code>.
     */
    public static SettingsLoader getInstance(){
        return instance;
    }

    /**
     * Returns an Api builder with the key documented in the settings file.
     */
    public DiscordApiBuilder getApiBuilder() throws JSONException{
        return new DiscordApiBuilder().setToken((String)this.settingsObject.get("api_token"));
    }

    /**
     * Creates bot commands to load into the scheduler.
     * @param api an api already connected to Discord
     * @return an array of bot commands to load into the scheduler
     */
    public BotCommand[] getBotCommands(DiscordApi api) throws JSONException{
        JSONArray events = this.settingsObject.getJSONArray("notifying_events");
        BotCommand[] commands = new BotCommand[events.length()];
        String name, jobType, jobData, cronString;
        User[] notifiedUsers;
        TextChannel[] targetChannels;
        Cron cron;

        try{
            for (int i = 0; i < events.length(); i++){
                name = (String)events.getJSONObject(i).get("name");
                jobType = (String)events.getJSONObject(i).get("job_type");
                jobData = (String)events.getJSONObject(i).get("job_data");
                notifiedUsers = parseUserList(api, ((JSONArray)events.getJSONObject(i).get("notified_users")).toList());
                targetChannels = parseChannelList(api, ((JSONArray)events.getJSONObject(i).get("target_channels")).toList());
                cron = this.parser.parse((String)events.getJSONObject(i).get("cron_string"));
                cron.validate();
                commands[i] = buildBotCommand(api, name, jobType, jobData, cron, notifiedUsers, targetChannels);
            }
        } catch (IllegalArgumentException e){
            throw new JSONException("Error in Cron String: " + e.getMessage());
        }
        return commands;
    }

    private BotCommand buildBotCommand(DiscordApi api, String name, String jobType, String jobData, Cron cron, User[] notifiedUsers, TextChannel[] targetChannels) throws JSONException{
        BotCommand command = null;
        if (jobType.equals("file-transfer")){
            command = new FileTransferToDiscordCommand(api, name, jobData, cron.asString(), this.descriptor.describe(cron), notifiedUsers, targetChannels);
        }
        return command;
    }

    private User[] parseUserList(DiscordApi api, List<Object> data) throws JSONException{
        User[] returnData = new User[data.size()];
        try {
            for (int i = 0; i < data.size(); i++){
                returnData[i] = api.getUserById(Long.parseLong((String)data.get(i))).get();
            }
        } catch (NumberFormatException e){
            throw new JSONException("Error parsing user list.");
        } catch (ExecutionException e){
            System.out.println(e.getCause());
            throw new JSONException("Invalid User Value or spontaneously cancelled.");
        } catch (InterruptedException e){
            //do nothing as it's likely a user break
        } catch (CancellationException e){
            //do nothing as it's likely a user break
        }
        return returnData;
    }

    private TextChannel[] parseChannelList(DiscordApi api, List<Object> data){
        TextChannel[] returnData = new TextChannel[data.size()];
        try{
            for (int i = 0; i < data.size(); i++){
                returnData[i] = api.getChannelById(Long.parseLong((String)data.get(i))).get().asTextChannel().get();
            }
        } catch (NumberFormatException e){
            throw new JSONException("Error parsing channel list.");
        } catch (NoSuchElementException e){
            throw new JSONException("Error loading channel.  Channel not found.");
        }
        return returnData;
    }

    public void load() throws FileNotFoundException, IOException{
        StringBuilder sb = new StringBuilder();
        String line;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream("/opt/alfredbot/bot-settings.json")));
        
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line);
        }
        
        this.settingsObject = new JSONObject(sb.toString());
    }
}
