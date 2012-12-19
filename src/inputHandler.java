/**
 * The input handler - tracks mouse and keyboard inputs
 *@author Matt Du
 */

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JOptionPane;


public class inputHandler implements KeyListener
{
	private Robot robot;
	
	private int width, height;
	private int mouseX, mouseY;
	
	private boolean fwd, bwd, lft, rgt, up, dwn, fly;
	
	public inputHandler(Component comp){
		try{
			robot = new Robot();
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			width = (int)screenSize.getWidth(); height = (int)screenSize.getHeight();
			robot.mouseMove(width/2, height/2);
		}
		catch (AWTException e){
			JOptionPane.showMessageDialog(null, "PICNIC Error: Problem in Chair, Not in Computer", "Error ID:107", JOptionPane.ERROR_MESSAGE);
		}
		
		comp.addKeyListener(this);
	}
	
	public void getMouse(){
		Point mousePos = MouseInfo.getPointerInfo().getLocation();
		
		mouseX = (int)mousePos.getX()-width/2;
		mouseY = (int)mousePos.getY()-height/2;
		
		robot.mouseMove(width/2, height/2);
	}
	
	public int getX(){
		return mouseX;
	}
	
	public int getY(){
		return mouseY;
	}
	
	public boolean getFwd(){
		return fwd;
	}
	
	public boolean getBwd(){
		return bwd;
	}
	
	public boolean getLft(){
		return lft;
	}
	
	public boolean getRgt(){
		return rgt;
	}
	
	public boolean getUp(){
		return up;
	}
	
	public boolean getDwn(){
		return dwn;
	}
	
	public boolean getFly(){
		return fly;
	}

	public void keyPressed(KeyEvent e){
		//Quit
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
			System.exit(0);
		
		//Handle movement keys
		if (e.getKeyCode() == KeyEvent.VK_W)
			fwd = true;
		if (e.getKeyCode() == KeyEvent.VK_S)
			bwd = true;
		if (e.getKeyCode() == KeyEvent.VK_A)
			lft = true;
		if (e.getKeyCode() == KeyEvent.VK_D)
			rgt = true;
		if (e.getKeyCode() == KeyEvent.VK_SPACE)
			up =  true;
		if (e.getKeyCode() == KeyEvent.VK_SHIFT)
			dwn = true;
		if (e.getKeyCode() == KeyEvent.VK_F)
			fly=!fly;
	}

	public void keyReleased(KeyEvent e){
		//Handle movement keys
		if (e.getKeyCode() == KeyEvent.VK_W)
			fwd = false;
		if (e.getKeyCode() == KeyEvent.VK_S)
			bwd = false;
		if (e.getKeyCode() == KeyEvent.VK_A)
			lft = false;
		if (e.getKeyCode() == KeyEvent.VK_D)
			rgt = false;
		if (e.getKeyCode() == KeyEvent.VK_SPACE)
			up =  false;
		if (e.getKeyCode() == KeyEvent.VK_SHIFT)
			dwn = false;
		
	}

	public void keyTyped(KeyEvent e) {}
}
