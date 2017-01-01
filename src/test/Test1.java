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

import hwj1.BinaryTreeAdderImpl;
import hwj1.OnerousSumRun;
import treeAdder.*;

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
		buffer.offer(singleNode);
		List<Future<Integer>> futureResults = new ArrayList<>(nProc);
		List<Callable<Integer>> tasks = new ArrayList<>(nProc);
		for(int i=0; i<nProc; i++)
			tasks.add(new OnerousSumRun(buffer,nProc,counter));
		
		// the execution order is controlled by the JVM Thread Pool
		futureResults = executor.invokeAll(tasks);
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
		buffer.offer(simpleTree);
		List<Future<Integer>> futureResults = new ArrayList<>(nProc);
		List<Callable<Integer>> tasks = new ArrayList<>(nProc);
		for(int i=0; i<nProc; i++)
			tasks.add(new OnerousSumRun(buffer,nProc,counter));
		
		// the execution order is controlled by the JVM Thread Pool
		futureResults = executor.invokeAll(tasks);
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
		buffer.offer(balancedOrderedTree);
		List<Future<Integer>> futureResults = new ArrayList<>(nProc);	
		List<Callable<Integer>> tasks = new ArrayList<>(nProc);
		for(int i=0; i<nProc; i++)
			tasks.add(new OnerousSumRun(buffer,nProc,counter));
		
		futureResults = executor.invokeAll(tasks);
		int sum = 0;
		for(Future<Integer> fu: futureResults)
			sum += fu.get();
		
		executor.shutdown();
		assertEquals(sum,31*32/2);
	}
	
	@Test
	public void ifBufferEmptyThreadWait() throws Exception {
		Future<Integer> f1;
		boolean asleep, awake;
		
		f1 = executor.submit(new OnerousSumRun(buffer,2,counter));
		int sum = 0;
		
		Thread.sleep(50);
		buffer.offer(balancedOrderedTree);
		Thread.sleep(50);
		asleep = this.buffer.isEmpty();
		
		synchronized (this.counter) { this.counter.notify(); }
		Thread.sleep(50);
		awake = this.buffer.isEmpty();
		
		
		synchronized (this.counter) { 
			this.counter.incrementAndGet();
			this.counter.notify(); 
			}
		sum += f1.get();
		executor.shutdown();
		
		assertFalse(asleep);
		assertTrue(awake);
		assertEquals(sum,31*32/2);
	}
	
	@Test
	public void theLastWakeEveryone() throws Exception {
		Future<Integer> f,f4;
		
		List<Future<Integer>> futureResults = new ArrayList<>(3);	
		for(int i=0; i<3; i++) {
			f = executor.submit(new OnerousSumRun(buffer,4,counter));
			futureResults.add(f);
		}
		
		Thread.sleep(50);
		buffer.offer(singleNode);
		
		f4 = executor.submit(new OnerousSumRun(buffer,4,counter));
		Thread.sleep(50);
		for(Future<Integer> fu: futureResults)
			assertTrue(fu.isDone());
		assertTrue(f4.isDone());
		
		executor.shutdown();
	}
	
	// un thread aggiunge un nodo nel buffer e sveglia un altro thread
	@Test
	public void newNodeInBufferWakeUpSomeone() throws Exception {
		Future<Integer> f;

		f = executor.submit(new Callable<Integer>() {
			public Integer call() throws Exception {
				synchronized (counter) {counter.wait();}
				return 1;
			}
		});
		//un nodo con un solo figlio
		buffer.offer(new simpleNode(1,new simpleNode(1),null));

		executor.submit(new OnerousSumRun(buffer,2,counter));
		Thread.sleep(50);
		assertEquals((int)f.get(),1);
		
		executor.shutdownNow();
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
