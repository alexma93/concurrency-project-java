package speedup;

public class Speedup {

	public static void main(String args[]) {
		double startTime, endTime;
		startTime = System.currentTimeMillis();
		
		int[] heights = {11,15,18};
		int[] heightsUnbalanced = {17,22};
		System.out.println("SPEED-UP SOLUZIONE 1\n");
		Speedup1.speedUp(heights,heightsUnbalanced);
		System.out.println("SPEED-UP SOLUZIONE 2\n");
		Speedup2.speedUp(heights,heightsUnbalanced);
		System.out.println("SPEED-UP SOLUZIONE 3\n");
		Speedup3.speedUp(heights,heightsUnbalanced);
		System.out.println("SPEED-UP SOLUZIONE 4\n");
		Speedup4.speedUp(heights,heightsUnbalanced);
		
		endTime = System.currentTimeMillis();
		System.out.println("\nTotal Time: "+(endTime-startTime)+" seconds");
	}
}
