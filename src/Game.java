import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javax.media.opengl.GLCapabilities;
import javax.swing.JFrame;


//Creates a window that draws the room.
public class Game{
	private static int  worldLength = 2000, worldWidth=2000;
	private static long worldSeed = 2406787912L;
	public static void main(String[] args){
		JFrame frame = new JFrame("Welcome to Kageshima");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//Invisible cursor
		BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "Invisimouse"); 
		frame.setCursor(blankCursor);		
		perlinMap p = new perlinMap(worldLength, worldWidth, 5, worldSeed);
//		new perlinTester(p).setVisible(true);
		Renderer r= new Renderer(new GLCapabilities(), p.getTerrain());
		frame.add(r);
		r.setCursor(blankCursor);
		
		frame.pack();
		frame.setLocation(0, 0);
		frame.setVisible(true);
	}
}
