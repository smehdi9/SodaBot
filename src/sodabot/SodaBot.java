/* TO DO LIST
 * 
 * Checkers:
 * - Figure out image in rich embed ?????
 * - Board labels - Make board pretty gradient
 * - Fix all messages make them good (Rich embeds)
 * - Delete old image after new
 * - Add help for checkers
 * - Add the check for second move after capture
 * - Timeout thread
 * 
 * - Brain storm other commands
 */

package sodabot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import soda.checkers.CheckersAssets;
import soda.checkers.CheckersCommandListener;

public class SodaBot {


	public static String token = "TOKEN";
	public static long botId = 744228015516418168L;

	public static JDA jda;
	
	public static boolean disableAlbania;
	public static String prefix = "a!";

	//Entry ------------------
	public static void main(String[] args) throws LoginException {
		JDABuilder builder = JDABuilder.createDefault(token);
		builder.setActivity(Activity.playing("Alb*nia 🤮"));
		jda = builder.build();
		
		//Initiation
		CheckersAssets.init();
		
		disableAlbania = true;
		jda.addEventListener(new AlbaniaListener());
		jda.addEventListener(new CheckersCommandListener());
		
	}
	
	public static void saveImage(BufferedImage img, String name) {
		File outputfile = new File(name);
		try {
			ImageIO.write(img, "png", outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

//Default Anti albania class - Albania </3
class AlbaniaListener extends ListenerAdapter {

  public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
	  	if(!SodaBot.disableAlbania) return;
		String msgContent = e.getMessage().getContentRaw().toLowerCase();

		if(msgContent.contains("albania")) {
			e.getChannel().deleteMessageById(e.getMessageId()).queue();
		}

		if(msgContent.contains("🇦🇱")) {
			e.getChannel().deleteMessageById(e.getMessageId()).queue();
		}
		
	}

	public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent e) {
	  	if(!SodaBot.disableAlbania) return;
		if(e.getReactionEmote().getName().equals("🇦🇱")) {
			e.getReaction().clearReactions().queue();
		}
	}

}
