package test;
import static org.junit.Assert.*;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.*;

import hwj3.BinaryTreeAdderForkJoin;
import hwj3.OnerousSumForkJoin;
import tree.*;

public class Test3 {

	Node singleNode, simpleTree, balancedOrderedTree;
	BlockingQueue<Node> buffer;
	ForkJoinPool pool;
	BinaryTreeAdder adder;
	
	@Before
	public void setup(){
		singleNode = new simpleNode(1,null,null);
		simpleTree = TreeUtility.balancedTree(3);
		balancedOrderedTree = TreeUtility.balancedOrderedTree(4);
		buffer = new LinkedBlockingQueue<Node>();
		pool = new ForkJoinPool(); 
		adder = new BinaryTreeAdderForkJoin();
	}
	
	@Test
	public void concurrentSumSingleNode() throws Exception {
		int sum;
		try {
			sum = pool.invoke(new OnerousSumForkJoin(singleNode));
		} finally {pool.shutdown();}
		assertEquals(sum,1);
	}
	

	@Test
	public void concurrentSumSimpleTree() throws Exception {
		int sum;
		try {
			sum = pool.invoke(new OnerousSumForkJoin(simpleTree));
		} finally {pool.shutdown();}		
		assertEquals(sum,15);
	}
	
	@Test
	public void concurrentSumOrderedTree() throws Exception {
		int sum;
		try {
			sum = pool.invoke(new OnerousSumForkJoin(balancedOrderedTree));
		} finally {pool.shutdown();}
		assertEquals(sum,31*32/2);
	}
	
	@Test
	public void binaryTreeAdderOrderedTree() throws Exception {
		int sum = adder.computeOnerousSum(balancedOrderedTree);
		assertEquals(sum,31*32/2);
	}
	
	@Test
	public void binaryTreeAdderUnbalancedLeftTree() throws Exception {
		int sum = adder.computeOnerousSum(TreeUtility.unbalancedLeftTree(4));
		assertEquals(sum,12);
	}
	
	@Test
	public void binaryTreeAdderUnbalancedRigthTree() throws Exception {
		int sum = adder.computeOnerousSum(TreeUtility.unbalancedRightTree(4));
		assertEquals(sum,12);
	}
	
	
	
}
