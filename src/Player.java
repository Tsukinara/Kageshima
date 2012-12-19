/**
 * The player class. It stores the location and movement of the player
 * @author Matt Du
 */
import javax.media.opengl.glu.GLU;


public class Player{

	public double x, y, z;
	private double hEye;
	private double horiz, vert;

	private boolean flying;

	public double xVel, zVel, speed, fly;

	public final double gravity = -10;
	private double yVel;
	public double jumpVel;

	private double accel;
	
	private Engine engine;

	protected double height, width, length;
	
	public Player(double xPos, double yPos, double zPos){
		x = xPos; y = yPos; z = zPos;
		hEye = 1.5;
		horiz = 0; vert = 0;
		yVel=0;
		height=4; width=10; length=10;
		flying = true;
		xVel = 0; zVel = 0;
		accel = 10;
		jumpVel=3;
		speed = 1;
		fly = 1;
		engine = null;
	}

	public Player(double xPos, double yPos, double zPos, Engine e){
		x = xPos; y = yPos; z = zPos;
		hEye = 1.5;
		horiz = 0; vert = 0;
		yVel=0;
		height=4; width=10; length=10;
		flying = true;
		xVel = 0; zVel = 0;
		accel = 10;
		jumpVel=3;
		speed = 1;
		fly = 1;
		engine = e;
	}
	
	public void adjust(GLU glu){
		glu.gluLookAt(x, y+hEye, z,
				x-Math.sin(horiz)*Math.cos(vert), y+hEye+Math.sin(vert),
				z+Math.cos(horiz)*Math.cos(vert),
				Math.sin(horiz)*Math.sin(vert), Math.cos(vert), -Math.cos(horiz)*Math.sin(vert));
	}

	public void step(inputHandler input, double dt){
		input.getMouse();
		horiz += 0.004*input.getX();
		vert -= 0.004*input.getY();
		if (vert > Math.PI/2) vert = Math.PI/2;
		if (vert < -Math.PI/2) vert = -Math.PI/2;

		applyKeyboardControls(input, dt);

		x += xVel*dt; 	z += zVel*dt;
	
	}

	private void applyKeyboardControls(inputHandler input, double dt){
		boolean fwd, bwd, lft, rgt, up, dwn;
		fwd = input.getFwd(); 	bwd = input.getBwd();
		lft = input.getLft(); 	rgt = input.getRgt();
		up = input.getUp();		dwn = input.getDwn();
		flying = input.getFly();

		if (fwd && bwd) {fwd = false; bwd = false;}
		if (lft && rgt) {lft = false; rgt = false;}
		if (up && dwn) {up = false; dwn = false;}

		if (up)
			if(flying){	y+=fly; yVel=0;}
			else if(!flying && y <= .01) {yVel=jumpVel; y=.01337;}
		if (dwn&&flying)	y = y<=0?0:y-fly;

		if (fwd && rgt) 		accelerate(-0.7*speed, 0.7*speed, dt);
		else if (fwd && lft)	accelerate(0.7*speed, 0.7*speed, dt);
		else if (bwd && rgt) 	accelerate(-0.5*speed, -0.5*speed, dt);
		else if (bwd && lft) 	accelerate(0.5*speed, -0.5*speed, dt);

		else if (fwd) 			accelerate(0, speed, dt);
		else if (bwd) 			accelerate(0, -0.5*speed, dt);
		else if (lft) 			accelerate(0.7*speed, 0, dt);
		else if (rgt) 			accelerate(-0.7*speed, 0, dt);

		else 					accelerate(0, 0, dt);
		if(!flying)				gravity(dt);
	}

	private void accelerate(double xS, double zS, double dt){
		double xMax = xS*Math.cos(horiz) - zS*Math.sin(horiz);
		double zMax = xS*Math.sin(horiz) + zS*Math.cos(horiz);

		double dx = xMax - xVel;
		double dz = zMax - zVel;
		double d = Math.sqrt(dx*dx + dz*dz);

		if (d < accel*dt){
			xVel = xMax;
			zVel = zMax;
		}
		else{
			xVel += dx/d * accel*dt;
			zVel += dz/d * accel*dt;
		}
	}

	private void gravity(double dt){
		if(y!=0 || y==.01337){
			yVel=yVel+gravity*dt;
			if(y+yVel*dt<=0){yVel=0; y = 0;}
			y+=yVel*dt;
		}
		runEngine();
	}
	
	private void runEngine(){
		if (engine == null)
			return;
		y = engine.detectCollisionAndCalc(this, 5) - 3;
	}
	
//	private boolean inRoom(){
//		boolean flag;
//		if(x<width && x > 0 && z < length && z > 0)
//			flag=true;
//		else flag=false;
//		System.out.println(flag);
//		return flag;
//	}
}
