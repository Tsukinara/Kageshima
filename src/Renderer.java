import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import javax.swing.JOptionPane;

import com.sun.opengl.util.FPSAnimator;
import com.sun.opengl.util.j2d.TextRenderer;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureIO;
//import com.sun.opengl.util.GLUT;

/**
 *This class makes a Renderer.
 *@author Matt Du
 */
public class Renderer extends GLCanvas{

	private static final long serialVersionUID = -5149203671121670529L;

	private float[][] terrain;

	private int worldLength, worldWidth;
	private float min, max;
	private inputHandler input;
	private Player player;

	private int renderDistance = 50;
	private TextRenderer tr;
	private GL gl;

	private double scale = .4;
	FPSAnimator anim;
	
	private Texture ground;

	/**
	 * Constructs the Renderer object. All initialization methods are called in this method.
	 * @param glc
	 */
	public Renderer(GLCapabilities glc, float[][]terrain){
		super(glc);
		this.terrain = terrain;
		worldLength = terrain.length; worldWidth=terrain[1].length;
		setPreferredSize(new Dimension(800, 800));
		initialize();
		addGLEventListener(new GLEventListener(){
			/**
			 * Steps and draws the scene.
			 */
			public void display(GLAutoDrawable drawable){
				gl = drawable.getGL();
				step();
				draw(gl);
			}

			public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,boolean deviceChanged){

			}

			/**
			 * Initializes OpenGL-related settings for the program.
			 */
			public void init(GLAutoDrawable drawable){
				GL gl = drawable.getGL();
				gl.glClearColor(0, 0, 0, 0);
				gl.glEnable(GL.GL_DEPTH_TEST);

				gl.glEnable(GL.GL_LIGHTING);
				gl.glEnable(GL.GL_LIGHT0);
				gl.glEnable(GL.GL_LIGHT1);

				gl.glLightModeli(GL.GL_LIGHT_MODEL_LOCAL_VIEWER, GL.GL_TRUE);
				gl.glLightModelfv(GL.GL_LIGHT_MODEL_AMBIENT, new float[]{0.03f,0.03f,0.03f,1}, 0);
				gl.glEnable(GL.GL_FRAMEBUFFER_SRGB_EXT);
				
				ground = createTexture("textures\\ground.jpg", gl);
			}

			public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height){				
			}

		});

		anim = new FPSAnimator(this, 60);
		anim.start();
	}

	/**
	 * Sets up everything, including modifiable variables, the input, and the player view.
	 */
	public void initialize(){
		input = new inputHandler(this);
		player = new Player(3, 100, 3, new Engine(this));
		maxMin();
	}

	public Texture createTexture(String fName, GL gl){
		Texture t=null;
		try{
			t = TextureIO.newTexture(new File(fName), true);
			t = TextureIO.newTexture(TextureIO.newTextureData(new File(fName), GL.GL_LUMINANCE, GL.GL_RGB, true, ".jpg"));
		}catch(Exception e){
			JOptionPane.showMessageDialog(null, "You dun' goofed", "Failure", JOptionPane.ERROR_MESSAGE);
		}
		gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
		gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);


		return t;
	}

	/**
	 * Performs a step of the simulation
	 */
	public void step(){		
		//Perform player controls.
		player.step(input, .05);
	}

	/**
	 * Renders the scene.
	 * @param gl
	 */
	public void draw(GL gl){
		GLU glu = new GLU();

		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(60, (double)getWidth()/getHeight(), 0.1, 500);

		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();
		player.adjust(glu);

		drawLights(gl);
		gl.glColor3d(1, 0, 0);

		createTerrain(gl);
		renderHelpText();
	}

	/**
	 * Makes the lights for the scenes
	 * @param gl
	 */
	public void drawLights(GL gl){
		//Lamp light
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, new float[]{0, max, 0, 1}, 0);

		gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, new float[]{0.8f, 0.8f, 0.8f, 1}, 1);gl.glLightfv(GL.GL_LIGHT0, GL.GL_SPECULAR, new float[]{1,1,1,1}, 0);
		gl.glLightf(GL.GL_LIGHT0, GL.GL_QUADRATIC_ATTENUATION, 0.1f);
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_AMBIENT, new float[]{0,0,0,1}, 0);

		//Sunlight
		gl.glLightfv(GL.GL_LIGHT1, GL.GL_POSITION, new float[]{0, max, 0, 1}, 0);
		gl.glLightfv(GL.GL_LIGHT1, GL.GL_DIFFUSE, new float[]{0.4f, 0.4f, 0.4f, 1}, 0);
		gl.glLightfv(GL.GL_LIGHT1, GL.GL_SPECULAR, new float[]{0.4f, 0.4f, 0.4f, 1}, 0);
	}


	private void createTerrain(GL gl){
		gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_SPECULAR, new float[] {0,0,0,1}, 1);
		int xT = worldLength/2, zT = worldWidth/2;
		ground.enable();	ground.bind();
		double s = 20;
		for(int i = 0; i < terrain.length-1; i++){
			for(int j = 0; j < terrain[i].length-1; j++){
				if(i-xT<player.x+renderDistance && i-xT > player.x-renderDistance && j-zT < player.z+renderDistance && j-zT > player.z-renderDistance){
					float percent = (terrain[i][j]-min)/(max-min);
					gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_DIFFUSE, getColor(new Color(0, percent*.5f, percent)), 0);
					gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_EMISSION, new float[] {0, percent, percent, 1}, 1);
					gl.glBegin(GL.GL_TRIANGLE_STRIP);
					gl.glTexCoord2d( i/s, 		j/s);
					gl.glVertex3f(	 i-xT, 		(int)((terrain[i][j]-min)*scale), 		j-zT);
					gl.glTexCoord2d((i+1)/s, 	j/s);
					gl.glVertex3f(	 i+1-xT, 	(int)((terrain[i+1][j]-min)*scale),  	j-zT);
					gl.glTexCoord2d( i/s,		(j+1)/s);
					gl.glVertex3f(	 i-xT, 		(int)((terrain[i][j+1]-min)*scale), 	j+1-zT);
					gl.glTexCoord2d((i+1)/s,	(j+1)/s);
					gl.glVertex3f(   i+1-xT, 	(int)((terrain[i+1][j+1]-min)*scale),	j+1-zT);
					gl.glEnd();
				}
			}
		}
		ground.disable();
	}


	public float[] getColor(Color c){
		return new float[]{c.getRed()/255f, c.getGreen()/255f, c.getBlue()/255f, c.getAlpha()/255f};
	}

	public void maxMin(){
		max = terrain[0][0];
		min = terrain[0][0];
		for(int i = 0; i < terrain.length; i++)
			for(int j = 0; j < terrain[i].length; j++){
				if(terrain[i][j]>max)
					max = terrain[i][j];
				if(terrain[i][j]<min)
					min = terrain[i][j];
			}
	}
	
	public double[] getWorldParams(){
		return new double[]{worldLength, worldWidth, min, max, scale};
	}
	
	public float[][] getTerrain(){
		return terrain;
	}
	
	private void renderHelpText(){
		tr = new TextRenderer(new Font("Copperplate Light", Font.ITALIC, 36));
		tr.beginRendering(getWidth(), getHeight());
		tr.setColor(1f, 1f, 1f, 1f);
		tr.draw("Camera Location: (" + (int)player.x + ", " + (int)player.y + ", " + (int)player.z + ")", 0, 5);
		tr.endRendering();
	}
}
