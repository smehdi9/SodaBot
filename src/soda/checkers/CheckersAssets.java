package soda.checkers;

import java.awt.image.BufferedImage;

public class CheckersAssets {
	
	
	public static final int SIZE = 100;
	
	public static BufferedImage boardImg;
	public static BufferedImage blackPce, redPce;
	public static BufferedImage blackPceKing, redPceKing;
	
	public static void init() { 
		
		//Set up board image
		boardImg = new BufferedImage(SIZE * 8, SIZE * 8, BufferedImage.TYPE_INT_ARGB);
		
		//--Some stupid code for the gradient
		//int diff = SIZE * 16;
		//start = new Color(0xFF9CEE, true), end = new Color(0xBB2CDB, true);
		//--End of some stupid code for the gradient

	    for(int y = 0; y < boardImg.getHeight(); y++) {
	      for(int x = 0; x < boardImg.getWidth(); x++) {
	        if(((x/SIZE) + (y/SIZE)) % 2 == 0) {
	        	boardImg.setRGB(x, y, 0xFF000000);
	        }
	        else boardImg.setRGB(x, y, 0xFFFFFFFF);
	      }
	    }
	    
	    //Set up pieces images
	    blackPce = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
	    fillCircle(blackPce, 1, SIZE - 2, 0xFF000000);
	    
	    redPce = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
	    fillCircle(redPce, 1, SIZE - 2, 0xFFFF0000);
	    
	    blackPceKing = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
	    fillCircle(blackPceKing, 1, SIZE - 2, 0xFF000000);
	    fillCircle(blackPceKing, 20, SIZE - 20, 0xFFFFFFFF);
	    
	    redPceKing = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
	    fillCircle(redPceKing, 1, SIZE - 2, 0xFFFF0000);
	    fillCircle(redPceKing, 20, SIZE - 20, 0xFFFFFFFF);
	}
	
	
	public static void fillCircle(BufferedImage img, int start, int end, int RGB) {
		int r = (end - start)/2;
		int centre = start + r;
		for(int x = start; x <= end; x++) {
			for(int y = start; y <= end; y++) {
				int cX = x - centre, cY = y - centre;
				if(cX * cX + cY * cY <= r * r) img.setRGB(x, y, RGB);
			}
		}
	}

}
