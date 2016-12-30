package test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.*;
import tree.*;

public class Test1 {

	Node singleNode, simpleTree, balancedOrderedTree;
	BlockingQueue<Node> buffer;
	Callable<Integer> serialOnerousSum;
	ExecutorService executor;
	AtomicInteger counter;
	BinaryTreeAdder adder;
	int nProc;
	
	@Before
	public void setup(){
		nProc = java.lang.Runtime.getRuntime().availableProcessors();
		singleNode = new simpleNode(1,null,null);
		simpleTree = TreeUtility.balancedTree(3);
		balancedOrderedTree = TreeUtility.balancedOrderedTree(4);
		buffer = new LinkedBlockingQueue<Node>();
		serialOnerousSum = new OnerousSumRun(buffer,1,new AtomicInteger());
		counter = new AtomicInteger();
		executor = Executors.newFixedThreadPool(nProc); 
		adder = new BinaryTreeAdderImpl(nProc);
	}
	
	@Test
	public void serialSumEmptyQueue() throws Exception {
		assertEquals(serialOnerousSum.call(),new Integer(0));

	}
	
	@Test
	public void serialSumSingleNode() throws Exception {
		
		buffer.offer(singleNode);
		assertEquals(serialOnerousSum.call(),new Integer(1));
	}
	
	@Test
	public void concurrentSumSingleNode() throws Exception {
		Future<Integer> f;
		buffer.offer(singleNode);
		List<Future<Integer>> futureResults = new ArrayList<>(nProc);
		for(int i=0; i<nProc; i++) {
			f = executor.submit(new OnerousSumRun(buffer,nProc,counter));
			futureResults.add(f);
		}
		int sum = 0;
		for(Future<Integer> fu: futureResults)
			sum += fu.get();
		executor.shutdown();
		Thread.sleep(10);
		
		assertEquals(sum,1);
		assertTrue(executor.isTerminated());
	}
	
	@Test
	public void serialSumSimpleTree() throws Exception {
		
		buffer.offer(simpleTree);
		assertEquals(serialOnerousSum.call(),new Integer(15));
	}
	
	@Test
	public void concurrentSumSimpleTree() throws Exception {
		Future<Integer> f;
		buffer.offer(simpleTree);
		List<Future<Integer>> futureResults = new ArrayList<>(nProc);
		for(int i=0; i<nProc; i++) {
			f = executor.submit(new OnerousSumRun(buffer,nProc,counter));
			futureResults.add(f);
		}
		int sum = 0;
		for(Future<Integer> fu: futureResults)
			sum += fu.get();
		
		executor.shutdown();
		Thread.sleep(10);
		
		assertEquals(sum,15);
		assertTrue(executor.isTerminated());
	}
	
	@Test
	public void serialSumOrderedTree() throws Exception {
		buffer.offer(balancedOrderedTree);
		assertEquals(serialOnerousSum.call(),new Integer((31*32/2))); //la sommatoria da 1 a 31
	}
	
	@Test
	public void concurrentSumOrderedTree() throws Exception {
		Future<Integer> f;
		buffer.offer(balancedOrderedTree);
		List<Future<Integer>> futureResults = new ArrayList<>(nProc);
		for(int i=0; i<nProc; i++) {
			f = executor.submit(new OnerousSumRun(buffer,nProc,counter));
			futureResults.add(f);
		}
		int sum = 0;
		for(Future<Integer> fu: futureResults)
			sum += fu.get();
		
		executor.shutdown();
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
