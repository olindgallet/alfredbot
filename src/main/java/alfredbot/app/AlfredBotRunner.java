package main.java.alfredbot.app;

import java.io.File;
import java.io.FilenameFilter;
/**
 * @author Olin Gallet
 * @date 22 September 2022
 * 
 * Runner class for the Alfred Bot Project.
 */

public class AlfredBotRunner{
	public static void main(String[] args){
		try{
			AlfredBot.getInstance().start();
		} catch (Exception e){
			System.out.println("Bot could not continue.");
			System.out.println(e);
			System.exit(1);
		}
	} 
}
