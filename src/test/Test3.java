package test;
import static org.junit.Assert.*;
import org.junit.*;
import java.util.concurrent.ForkJoinPool;
import hwj3.BinaryTreeAdderForkJoin;
import hwj3.OnerousSumForkJoin;
import treeAdder.*;

public class Test3 {

	Node singleNode, simpleTree, balancedOrderedTree;
	ForkJoinPool pool;
	BinaryTreeAdder adder;
	
	@Before
	public void setup(){
		singleNode = new SimpleNode(1,null,null);
		simpleTree = TreeUtility.balancedTree(3);
		balancedOrderedTree = TreeUtility.balancedOrderedTree(4);
		pool = new ForkJoinPool(); 
		adder = new BinaryTreeAdderForkJoin();
	}
	
	@Test
	public void concurrentSumSingleNode() {
		int sum;
		try {
			sum = pool.invoke(new OnerousSumForkJoin(singleNode));
		} finally {pool.shutdown();}
		assertEquals(sum,1);
	}
	

	@Test
	public void concurrentSumSimpleTree() {
		int sum;
		try {
			sum = pool.invoke(new OnerousSumForkJoin(simpleTree));
		} finally {pool.shutdown();}		
		assertEquals(sum,15);
	}
	
	@Test
	public void binaryTreeAdderOrderedTree() {
		int sum = adder.computeOnerousSum(balancedOrderedTree);
		assertEquals(sum,31*32/2);
	}
	
	@Test
	public void binaryTreeAdderUnbalancedLeftTree() {
		int sum = adder.computeOnerousSum(TreeUtility.unbalancedLeftTree(4));
		assertEquals(sum,12);
	}
	
	@Test
	public void binaryTreeAdderUnbalancedRigthTree() {
		int sum = adder.computeOnerousSum(TreeUtility.unbalancedRightTree(4));
		assertEquals(sum,12);
	}
	
	
	
}
