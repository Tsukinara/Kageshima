import java.util.*;

public class perlinMap{
	private int width;
	private int height;
	private int roughness;
	private long seed;

	public perlinMap(int width, int height, int roughness){
		this(width, height, roughness, System.currentTimeMillis());
	}

	public perlinMap(int width, int height, int roughness, long seed){
		this.width = width;
		this.height = height;
		this.roughness = roughness;
		this.seed = seed;
	}

	public float[][] getTerrain(){
		double [][] tempResult = new double[width][height];
		for(int scale = roughness; scale < 8; ++scale){
			int [][] intermediate = generateOneOctave(width, height, seed + scale, 16);
			for(int x = 0; x < width; ++x)
				for(int y = 0; y < height; ++y)
					tempResult[x][y] += ((double)intermediate[x][y]) / (2 << (scale - roughness));
		}

		float [][] result = new float[width][height];
		for(int x = 0; x < width; ++x)
			for(int y = 0; y < height; ++y)
				result[x][y] = (float)((int)tempResult[x][y]);
		return result;
	}


	private int[][] generateOneOctave(int width, int height, long seed, double scale){
		ArrayList<ArrayList<Double>> G = new ArrayList<ArrayList<Double>>();
		Random r = new Random(seed);

		while(G.size() < 256){
			while(true)	{
				double first = r.nextDouble() * 2 - 1;
				double second = r.nextDouble() * 2 - 1;

				double length = Math.sqrt(first * first + second * second);
				if(length < 1.0){
					ArrayList<Double> newElem = new ArrayList<Double>();
					newElem.add(first / length);
					newElem.add(second / length);
					G.add(newElem);
					break;
				}
			}
		}

		int[] P = new int[256];
		for(int i = 0; i < P.length; i++){
			P[i] = i;
		}

		for(int i = P.length - 1; i > 0; i--){
			int index = r.nextInt(i);
			int temp = P[index];
			P[index] = P[i];
			P[i] = temp;
		}

		int[][] result = new int[width][height];
		for(int x = 0; x < width; ++x){
			for(int y = 0; y < height; ++y)	{
				result[x][y] = (int) ((noise(x / scale, y / scale, P, G) + 1) * 128);
			}
		}

		return result;
	}

	private double drop(double a){
		double b = Math.abs(a);
		return 1.0 - b * b * b * (b * (b * 6 - 15) + 10);
	}

	private double Q(double u, double v){
		return drop(u) * drop(v);
	}

	private double dotProduct(ArrayList<Double> b, double[] a){
		return a[0] * b.get(0) + a[1] * b.get(1);
	}

	private double noise(double x, double y, int[] P, ArrayList<ArrayList<Double>> G){
		double[] cell = new double[] {Math.floor(x), Math.floor(y)};

		double sum = 0.0;
		for(int r = 0; r <= 1; ++r)	{
			for(int s = 0; s <= 1; ++s){
				double i = cell[0] + r;
				double j = cell[1] + s;

				double[] uv = new double[] {x - i, y - j};

				int index = P[(int) i];
				index = P[(index + (int) j) % P.length];
				ArrayList<Double> grad = G.get(index % G.size());
				sum += Q(uv[0], uv[1]) * dotProduct(grad, uv);
			}
		}

		return Math.max(Math.min(sum, 1.0), -1.0);
	}
}
