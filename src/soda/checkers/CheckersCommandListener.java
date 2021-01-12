package soda.checkers;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import sodabot.SodaBot;

public class CheckersCommandListener extends ListenerAdapter {

	LinkedList<Checkers> checkersGames;

	public CheckersCommandListener() { 
		checkersGames = new LinkedList<Checkers>();
	}


	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		User author = e.getAuthor();
		if(author.isBot()) return;			//Return if talking to bot
		Message msg = e.getMessage();
		TextChannel ch = e.getChannel();
		String msgContent = msg.getContentRaw();
		String[] args = msgContent.split("\\s+");
		
		//Get channel game
		Checkers channelGame = null;
		if(checkersGames.size() > 0) channelGame = getGame(ch);


		//COMMAND ARGS FOR prefixcheckers COMMAND
		if(args[0].toLowerCase().equals(SodaBot.prefix + "checkers")) {
			File board = null;

			//No arguments
			if(args.length == 1) { 
				if(addGameToChannel(author, ch)) {
					ch.sendMessage(author.getAsMention() + " has joined a checkers game!").queue();
					if(channelGame != null && channelGame.hasBothPlayers()) {
						ch.sendMessage("Checkers game started!").queue();
						channelGame.startGame();
						SodaBot.saveImage(channelGame.drawBoard(), ch.getId() + "checkersboard.png");
						board = new File(ch.getId() + "checkersboard.png");

						ch.sendFile(board).queue();
					}
				}
				else {
					ch.sendMessage("There is already a game running!").queue();
				}
			}


			//One argument
			if(args.length == 2) {
				//Invalid move command
				if(args[1].toLowerCase().equals("move")) {
					ch.sendMessage("Add a move after that please").queue();
				}

				//Quit command
				if(args[1].toLowerCase().equals("exit")) {
					if(channelGame == null) {
						ch.sendMessage("There is no game currently running").queue();
					}
					else {
						if((channelGame.pOne != null && author.equals(channelGame.pOne)) || (channelGame.pTwo != null && author.equals(channelGame.pTwo))) {
							if(channelGame.running) {
								if(channelGame.pOne != null && author.equals(channelGame.pOne))  {
									ch.sendMessage("Black wins!").queue();
								}
								if(channelGame.pTwo != null && author.equals(channelGame.pTwo))  {
									ch.sendMessage("Red wins!").queue();
								}
							}
							else ch.sendMessage("Exiting game!").queue();
							checkersGames.remove(channelGame);
						}
					}
				}
			}


			//Two arguments
			if(args.length == 3) {
				//Make a move command
				if(args[1].toLowerCase().equals("move")) {
					if(channelGame == null) {
						ch.sendMessage("There is currently no game running.").queue();
					}
					else {
						// IF MOVE COMMAND INPUT IS WRONG like a!checkers move sjkrg 
						userMove(channelGame, ch, author, args[2], msg);
					}
				}
				
				
			}
		}
		
		//Move shorthand check - If one of the players types the right move they don't have to type out "checkers move" every time (ONLY FOR PLAYERS AND IF GAME IS RUNNING)
		if(channelGame != null) {
			if(msgContent.length() == 4) userMove(channelGame, ch, author, msgContent, msg);
		}
	}
	
	
	public void userMove(Checkers channelGame, TextChannel ch, User author, String input, Message msg) {
		File board = null;
		
		if((channelGame.turn && channelGame.pOne.getIdLong() == author.getIdLong()) || (!channelGame.turn && channelGame.pTwo.getIdLong() == author.getIdLong())) {
			String result = channelGame.makeMove(input);
			
			switch(result) {				
			case "nopiece":
				ch.sendMessage("No piece to move in selected location").queue();
				return;
				
			case "nogame":
				ch.sendMessage("Game has not started yet").queue();
				return;
				
			case "invalidpos":
				ch.sendMessage("You cannot move your piece there").queue();
				return;
				
			case "notking":
				ch.sendMessage("that piece cannot move backwards").queue();
				return;
				
			case "spaceocc":
				ch.sendMessage("There is a piece already there").queue();
				return;
				
			case "validmove":
				SodaBot.saveImage(channelGame.drawBoard(), ch.getId() + "checkersboard.png");
				board = new File(ch.getId() + "checkersboard.png");
				
				ch.sendMessage(author.getAsMention() + " moved to " + input.substring(2)).addFile(board).queue();
				
				removePreviousBoard(channelGame);
				
				return;
				
			case "redwin":
				ch.sendMessage("Red wins!").queue();
				checkersGames.remove(channelGame);
				return;
				
			case "blackwin":
				ch.sendMessage("Black wins!").queue();
				checkersGames.remove(channelGame);
				return;
			}
		}
	}


	public boolean addGameToChannel(User starter, TextChannel channel) { 
		for(Checkers chck : checkersGames) {
			if(chck.pOne != null && chck.pOne.equals(starter)) return false;
			if(chck.pTwo != null && chck.pTwo.equals(starter)) return false;
			if(chck.channel.getIdLong() == channel.getIdLong()) {
				if(chck.hasSecondPlayer()) {
					return false;
				}
				else {
					chck.pTwo = starter;
					return true;
				}
			}
		}
		checkersGames.add(new Checkers(starter, channel));
		return true;
	}


	public Checkers getGame(TextChannel ch) { 
		for(Checkers chck : checkersGames) {
			if(chck.channel.getIdLong() == ch.getIdLong()) return chck;
		}
		return null;
	}
	
	
	//WIP what the fuck is happpening pls thinnk this code through wtf..
	public void removePreviousBoard(Checkers game) {
		if(game.previousBoardMessage != null) {
			System.out.println(game.previousBoardMessage.getContentRaw());
			game.channel.deleteMessageById(game.previousBoardMessage.getIdLong()).queue();
		}
		
		Message newMsg = null;
		List<Message> messages = game.channel.getHistory().retrievePast(3).complete();
		for(Message m : messages) { 
			if(m.getAuthor().getIdLong() == SodaBot.botId && m.getAttachments().size() > 0) {
				newMsg = m;
				break;
			}
		}
		game.previousBoardMessage = newMsg;
	}

}
