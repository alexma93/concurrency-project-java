package speedup;

public class Speedup {

	public static void main(String args[]) {
		int[] heights = {10,14,17};
		int[] heightsUnbalanced = {15,20};
		System.out.println("SPEED-UP SOLUZIONE 1\n");
		Speedup1.speedUp(heights,heightsUnbalanced);
		System.out.println("SPEED-UP SOLUZIONE 2\n");
		Speedup2.speedUp(heights,heightsUnbalanced);
		System.out.println("SPEED-UP SOLUZIONE 3\n");
		Speedup3.speedUp(heights,heightsUnbalanced);
		System.out.println("SPEED-UP SOLUZIONE 4\n");
		Speedup4.speedUp(heights,heightsUnbalanced);
	}
}
