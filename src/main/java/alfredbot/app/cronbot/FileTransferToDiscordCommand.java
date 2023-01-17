package main.java.alfredbot.app.cronbot;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.mention.AllowedMentions;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.MessageDecoration;
import org.javacord.api.entity.user.User;
import main.java.alfredbot.app.cronbot.AbstractBotCommand;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FilenameFilter;

public class FileTransferToDiscordCommand extends AbstractBotCommand {
    public FileTransferToDiscordCommand(DiscordApi api, String name, String jobData, String cronString, String cronDescription, User[] notifiedUsers, TextChannel[] targetChannels){
        super(api, name, jobData, cronString, cronDescription, notifiedUsers, targetChannels);
    }

    public void perform() throws Exception{
        File[] files = getFiles();
        TextChannel[] textChannels = super.getTextChannels();
        User[] notifiedUsers = super.getNotifiedUsers();
        MessageBuilder messageBuilder = new MessageBuilder();
        if (!files.equals(null)){
            messageBuilder.setAllowedMentions(super.getAllowedMentions());

            for (User user: notifiedUsers){ messageBuilder.append(user.getMentionTag()); }
            messageBuilder.append(" Files Received:");
            for (File file: files) { messageBuilder.addAttachment(file); }
            for (TextChannel channel: textChannels) {messageBuilder.send(channel); }
        }
        System.out.println(files.length + " were uploaded to the channel.");
    }

    private File[] getFiles() throws IOException, FileNotFoundException{
        String file = super.getJobData();
        File[] files = new File[1];

        if (file.lastIndexOf("*.") == -1){
            files[0] = new File(file);
        } else {
            String ending = file.substring(file.lastIndexOf("*.") + 1, file.length());
            FilenameFilter filter = (d,s) -> {return s.toLowerCase().endsWith(ending); };
            
            if (file.lastIndexOf("/") == -1){
                files = new File(".").listFiles(filter);
            } else {
                files = new File(file.substring(0, file.lastIndexOf("/")) + "/").listFiles(filter);
            }
        } 	
        return files;
    }
}
