package soda.checkers;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class Checkers {
		
	
	//Pieces class
	class Piece {
		public int x, y;
		public boolean side;	//true = red, false = black
		public boolean isKing;
		public boolean isAlive;
		
		public Piece(int x, int y, boolean side) { 
			this.x = x;
			this.y = y;
			this.side = side;
			this.isAlive = true;
		}
		
		public String move(int toX, int toY) {
			//Completely forbidden moves
			if(Math.abs(toX - x) != Math.abs(toY - y)) {
				return "invalidpos";
			}
			if(Math.abs(toX - x) > 2 || Math.abs(toY - y) > 2) {
				return "invalidpos";
			}
			if(toX > 7 || toX < 0 || toY > 7 || toY < 0) {
				return "invalidpos";
			}
			
			for(Piece p : pieces) {
				if (!p.isAlive) continue;
				if (p.x == toX && p.y == toY) {
					return "spaceocc";
				}
			}
			
			boolean canTake = false;
			
			//Red player
			if(side) {
				if(!isKing && toY < y) {
					return "notking";
				}
				
				if(Math.abs(toY - y) == 2) {
					for(Piece p : pieces) {
						if (!p.isAlive) continue;
						if (p.side == side) continue;
						if (p.x == x + (toX - x)/2 && p.y == y + (toY - y)/2) {
							p.isAlive = false;
							canTake = true;
							break;
						}
					}
					
					if(!canTake) {
						return "invalidpos";
					}
				}
			}
			
			//Black player
			else {
				if(!isKing && toY > y) {
					return "notking";
				}
				
				if(Math.abs(toY - y) == 2) {
					for(Piece p : pieces) {
						if (!p.isAlive) continue;
						if (p.side == side) continue;
						if (p.x == x + (toX - x)/2 && p.y == y + (toY - y)/2) {
							p.isAlive = false;
							canTake = true;
							break;
						}
					}
					
					if(!canTake) {
						return "invalidpos";
					}
				}
			}
			
			//COMPLETED MOVE
			x = toX; y = toY;
			
			//Change to king
			if(side && y == 7) isKing = true;
			if(!side && y == 0) isKing = true;
			
			//Check for win
			String win = checkWin();
			if(!win.equals("nowin")) {
				return win;
			}
			
			if(canTake) return checkAnotherMove();
			return "validmove";
		}
		
		public String checkAnotherMove() {
			/*for(Piece p : pieces) {
				if(p.isAlive && p.side != side && p.x == x + 1 && p.x == x + 1 && getPiece(x + 2, y + 2, side) == null) {
					return 
				}
			}*/
			return "validmove";
		}
		
		public String checkWin() {
			int redp = 0, blackp = 0;
			for(Piece p : pieces) {
				if(p.side && p.isAlive) redp++;
				else if (!p.side && p.isAlive) blackp++;
			}
			if(redp == 0) return "blackwin";
			if(blackp == 0) return "redwin";
			return "nowin";
		}
		
		public boolean isEqual(Piece p) { 
			return(x == p.x && y == p.y && isKing == p.isKing && side == p.side);
		}
	}
	
	
	
	Piece[] pieces;
	boolean turn;		//pOne = false, pTwo = true;
	boolean running;	

	User pOne, pTwo;		//pOne = red, pTwo = black
	TextChannel channel;
	Message previousBoardMessage;	//The last message with a board sent in the channel. To be deleted when next message is sent.

	
	public Checkers(User p1, TextChannel channel) {
		pieces = new Piece[24];
		//Set up pieces
		for(int i = 0; i < 4; i++) {
			pieces[i] = new Piece(2 * i + 1, 0, true);
			pieces[i + 4] = new Piece(2 * i, 1, true);
			pieces[i + 8] = new Piece(2 * i + 1, 2, true);

			pieces[i + 12] = new Piece(2 * i, 5, false);
			pieces[i + 16] = new Piece(2 * i + 1, 6, false);
			pieces[i + 20] = new Piece(2 * i, 7, false);
		}
		
		turn = true;
		
		pOne = p1; pTwo = null;
		this.channel = channel;
		
		previousBoardMessage = null;
	}
	
	
	
	public boolean startGame() {
		if(pOne != null && pTwo != null) {
			running = true;
			return true;
		}
		return false;
	}
	

	public boolean hasBothPlayers() {
		return pOne != null && pTwo != null;
	}

	
	public boolean hasSecondPlayer() {
		return pTwo != null;
	}
	

	public String makeMove(String input) {
		if(!running) return "nogame";
		input = input.toUpperCase();
		
		if(!isValidInput(input)) return "invalidinput";
		
		int x1 = (int)input.charAt(0) - 65;
		int y1 = (int)input.charAt(1) - 49;
		
		int x2 = (int)input.charAt(2) - 65;
		int y2 = (int)input.charAt(3) - 49;
		
		Piece p = getPiece(x1, y1, turn);
		if(p == null) return "nopiece";
		
		String moveRet = p.move(x2, y2);
		if(moveRet.equals("validmove")) {
			turn = !turn;
		}
		return moveRet;
	}
	
	
	public boolean isValidInput(String input) {
		if(input.length() != 4) return false;
		if(!Character.isDigit(input.charAt(1)) || input.charAt(1) > 56 || !Character.isDigit(input.charAt(3)) || input.charAt(3) > 56 || input.charAt(0) < 65 || input.charAt(0) > 72 || input.charAt(2) < 65 || input.charAt(2) > 72) return false;
		
		return true;
	}
	

	public Piece getPiece(int x, int y, boolean currentTurn) {
		if(x > 7 || x < 0 || y > 7 || y < 0) return null;
		for(Piece p : pieces) { 
			if(p.x == x && p.y == y && currentTurn == p.side && p.isAlive) return p;
		}
		return null;
	}
	
	

	public BufferedImage drawBoard() { 		
		BufferedImage finalBrd = new BufferedImage(CheckersAssets.SIZE * 8, CheckersAssets.SIZE * 8, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D) finalBrd.getGraphics();
		g.drawImage(CheckersAssets.boardImg, 0, 0, null);
		for(Piece p : pieces) {
			if (!p.isAlive) continue;
			if (p.side) {
				if(p.isKing) g.drawImage(CheckersAssets.redPceKing, p.x * CheckersAssets.SIZE, p.y * CheckersAssets.SIZE, null);
				else g.drawImage(CheckersAssets.redPce, p.x * CheckersAssets.SIZE, p.y * CheckersAssets.SIZE, null);
			}
			else {
				if(p.isKing) g.drawImage(CheckersAssets.blackPceKing, p.x * CheckersAssets.SIZE, p.y * CheckersAssets.SIZE, null);
				else g.drawImage(CheckersAssets.blackPce, p.x * CheckersAssets.SIZE, p.y * CheckersAssets.SIZE, null);
			}
		}
		return finalBrd;
	}	
	
}
