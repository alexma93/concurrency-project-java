package speedup;

import hwj2.BinaryTreeAdderLimitedBuffer;
import tree.*;

public class Speedup2 {

	public static void main(String[] args) {
		Node tree;
		int serialSum, concurrentSum ;
		double startTime, endTime, serialTime, concurrentTime;
		int nProc = java.lang.Runtime.getRuntime().availableProcessors();
		BinaryTreeAdder serialAdder, concurrentAdder;
		serialAdder = new BinaryTreeAdderLimitedBuffer(1);
		concurrentAdder = new BinaryTreeAdderLimitedBuffer(nProc);
		
		System.out.println("WARM UP");
		tree = TreeUtility.balancedTree(14);
		concurrentAdder.computeOnerousSum(tree);
		concurrentAdder.computeOnerousSum(tree);

		int[] heights = {10,14,17};

		for(int i : heights) {
			System.out.println("Balanced Tree, height = "+i);
			tree = TreeUtility.balancedTree(i);
			
			startTime = System.currentTimeMillis();
			concurrentSum = concurrentAdder.computeOnerousSum(tree);
			endTime = System.currentTimeMillis();
			concurrentTime = endTime - startTime;
			
			System.out.println("Concurrent: sum="+concurrentSum+" concurrentTime="+concurrentTime);
			
			startTime = System.currentTimeMillis();
			serialSum = serialAdder.computeOnerousSum(tree);
			endTime = System.currentTimeMillis();
			serialTime = endTime - startTime;
			
			System.out.println("Serial: sum="+serialSum+" serialTime="+serialTime);
			System.out.println("Speed-up: "+ (serialTime/concurrentTime)+"\n");
		}

	}


}
