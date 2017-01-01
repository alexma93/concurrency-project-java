package speedup;

import hwj2.BinaryTreeAdderLimitedBuffer;
import hwj4.BinaryTreeAdderJ8;
import tree.*;

public class Speedup4 {

	public static void main(String[] args) {
		Node tree;
		int serialSum, concurrentSum ;
		double startTime, endTime, serialTime, concurrentTime;
		BinaryTreeAdder serialAdder, concurrentAdder;
		serialAdder = new BinaryTreeAdderLimitedBuffer(1);
		concurrentAdder = new BinaryTreeAdderJ8();
		
		System.out.println("WARM UP");
		tree = TreeUtility.balancedTree(14);
		concurrentAdder.computeOnerousSum(tree);
		concurrentAdder.computeOnerousSum(tree);

		int[] heights = {13};

		for(int i : heights) {
			System.out.println("Balanced Tree, height = "+i);
			tree = TreeUtility.balancedOrderedTree(i);
			
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
