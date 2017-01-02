package test;
import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.Spliterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.*;

import hwj4.BinaryTreeAdderJ8;
import hwj4.BinaryTreeSpliterator;
import treeAdder.*;

public class Test4 {

	Node singleNode, smallTree, simpleTree, balancedOrderedTree;
	BlockingQueue<Node> buffer;
	ForkJoinPool pool;
	BinaryTreeAdder adder;
	BinaryTreeSpliterator spliterator;
	
	@Before
	public void setup(){
		singleNode = new SimpleNode(1,null,null);
		smallTree = TreeUtility.balancedTree(1);
		simpleTree = TreeUtility.balancedTree(3);
		balancedOrderedTree = TreeUtility.balancedOrderedTree(4);
		buffer = new LinkedBlockingQueue<Node>();
		pool = new ForkJoinPool(); 
		adder = new BinaryTreeAdderJ8();
	}
	
	// TEST SULLO SPLITERATOR
	
	@Test
	public void trySplitSingleNode() throws Exception {
		spliterator = new BinaryTreeSpliterator(singleNode,new LinkedList<>());
		Spliterator<Node> spliterator2 = spliterator.trySplit();
		assertEquals(spliterator2,null);
	}
	
	@Test
	public void trySplitSmallTree() throws Exception {
		spliterator = new BinaryTreeSpliterator(smallTree,new LinkedList<>());
		BinaryTreeSpliterator spliterator2 = (BinaryTreeSpliterator) spliterator.trySplit();
		assertEquals(spliterator.getRoot(),smallTree.getSx());
		assertEquals(spliterator2.getRoot(),smallTree.getDx());
		assertTrue(spliterator2.getList().contains(smallTree));
		assertFalse(spliterator.getList().contains(smallTree));
	}
	// singolo figlio sul trysplit e' un caso molto semplice (non lo testo)
	
	@Test
	public void tryAdvanceSingleNode() throws Exception {
		spliterator = new BinaryTreeSpliterator(singleNode,new LinkedList<>());
		boolean advanced = spliterator.tryAdvance(n -> n.getValue());
		assertTrue(advanced);
		assertEquals(spliterator.getList().size(),0);
		assertEquals(spliterator.getRoot(),null);
	}
	
	@Test
	public void tryAdvanceEmptyList() throws Exception {
		spliterator = new BinaryTreeSpliterator(singleNode,new LinkedList<>());
		spliterator.tryAdvance(n -> n.getValue());
		boolean advanced = spliterator.tryAdvance(n -> n.getValue());
		assertFalse(advanced);
	}
	
	// Test generali
	
	@Test
	public void concurrentSumSingleNode() throws Exception {
		int sum = adder.computeOnerousSum(singleNode);
		assertEquals(sum,1);
	}
	

	@Test
	public void concurrentSumSimpleTree() throws Exception {
		int sum = adder.computeOnerousSum(simpleTree);
		assertEquals(sum,15);
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
