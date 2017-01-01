package speedup;

import hwj3.BinaryTreeAdderForkJoin;
import treeAdder.BinaryTreeAdder;
import treeAdder.BinaryTreeAdderSerial;
import treeAdder.Node;
import treeAdder.TreeUtility;

public class Speedup3 {

	public static void speedUp(int[] heights, int[] heightsUnbalanced) {
		Node tree;
		int serialSum, concurrentSum ;
		double startTime, endTime, serialTime, concurrentTime;
		BinaryTreeAdder serialAdder, concurrentAdder;
		serialAdder = new BinaryTreeAdderSerial();
		concurrentAdder = new BinaryTreeAdderForkJoin();
		
		System.out.println("WARM UP");
		tree = TreeUtility.balancedTree(15);
		concurrentAdder.computeOnerousSum(tree);
		concurrentAdder.computeOnerousSum(tree);

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
		
		System.out.println();
		for(int i : heightsUnbalanced) {
			System.out.println("Unbalanced Left Tree, height = "+i);
			tree = TreeUtility.unbalancedLeftTree(i);
			
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
		
		System.out.println();
		for(int i : heightsUnbalanced) {
			System.out.println("Unbalanced Right Tree, height = "+i);
			tree = TreeUtility.unbalancedRightTree(i);
			
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
