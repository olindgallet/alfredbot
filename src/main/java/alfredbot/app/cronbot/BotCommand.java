package main.java.alfredbot.app.cronbot;

/**
 * @author Olin Gallet
 * @date 3 Oct 2022
 */
public interface BotCommand {
    public void perform() throws Exception;

    public String getName();

    public String getCronString();

    public String getCronDescription();
}