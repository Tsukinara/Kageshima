import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JFrame;

public class perlinTester extends JFrame{
	private static final long serialVersionUID = 4854035374944221421L;
	private static int size = 800;
	private perlinMap pM2;
	private float min, max;
	private float[][] grid;
	public static void main(String [] args){new perlinTester().setVisible(true);}
	
	public perlinTester(){
		super("Perlin Noise Testing");
		pM2 = new perlinMap(size, size, 1, serialVersionUID);
		grid=pM2.getTerrain();
		setSize(grid.length, grid[0].length);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocation(50, 50);
		setResizable(false);
	}
	public perlinTester(perlinMap pM2){
		super("Perlin Noise Testing");
		this.pM2 = pM2;
		grid=pM2.getTerrain();
		setSize(grid.length, grid[0].length);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocation(50, 50);
		setResizable(false);
	}
	
	public void paint(Graphics g){
		super.paint(g);
		maxMin();
		for(int i = 0; i < grid.length; i++)
			for(int j = 0; j < grid[i].length; j++){
				double percent = (grid[i][j]+Math.abs(min))/(max+Math.abs(min)) * 255.0f;
				g.setColor(new Color((int)percent, (int)percent, (int)percent));
				g.drawLine(i, j, i, j);
			}
		
	}
	
	public void maxMin(){
		max = grid[0][0];
		max = grid[0][0];
		for(int i = 0; i < grid.length; i++)
			for(int j = 0; j < grid[i].length; j++){
				if(grid[i][j]>max)
					max = grid[i][j];
				if(grid[i][j]<min)
					min = grid[i][j];
			}
	}
}