
public class Engine {
	private Renderer r;
	public Engine(Renderer r){
		this.r = r;
	}
	public boolean detectCollision(Player p, int tolerance){
		double[] arr = r.getWorldParams();
		int xT = (int) arr[0] / 2;
		int zT = (int) arr[1] / 2;
		double min = arr[2];
		double scale = arr[4];
		if (Math.abs(p.x) < xT && Math.abs(p.z) < zT && p.y < (int)((r.getTerrain()[(int) (p.x + xT)][(int) (p.z + zT)]-min)*scale) + tolerance)
			return true;
		return false;
	}
	public double detectCollisionAndCalc(Player p, int tolerance){
		double[] arr = r.getWorldParams();
		int xT = (int) arr[0] / 2;
		int zT = (int) arr[1] / 2;
		double min = arr[2];
		double scale = arr[4];
		if (Math.abs(p.x) < xT && Math.abs(p.z) < zT && p.y <= (int)((r.getTerrain()[(int) (p.x + xT)][(int) (p.z + zT)]-min)*scale) + tolerance)
			return (int)((r.getTerrain()[(int) (p.x + xT)][(int) (p.z + zT)]-min)*scale) + tolerance;
		return p.y;
	}
}
