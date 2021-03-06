package test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.*;

import hwj2.BinaryTreeAdderLimitedBuffer;
import hwj2.OnerousSumRunLimited;
import treeAdder.*;

public class Test2 {

	Node singleNode, simpleTree, balancedOrderedTree;
	List<BlockingDeque<Node>> dequeList, serialList;
	Callable<Integer> serialOnerousSum;
	ExecutorService executor;
	AtomicInteger counter;
	BinaryTreeAdder adder;
	BlockingDeque<Node> serialBuffer;
	int nProc;
	
	@Before
	public void setup(){
		nProc = java.lang.Runtime.getRuntime().availableProcessors();
		singleNode = new SimpleNode(1,null,null);
		simpleTree = TreeUtility.balancedTree(3);
		balancedOrderedTree = TreeUtility.balancedOrderedTree(4);
		dequeList = new ArrayList<>(this.nProc);
		for(int i=0;i<this.nProc;i++)
			dequeList.add(new LinkedBlockingDeque<>());
		serialBuffer = new LinkedBlockingDeque<>();
		serialList = new ArrayList<>();
		serialOnerousSum = new OnerousSumRunLimited(serialBuffer,1,new AtomicInteger(),serialList);
		counter = new AtomicInteger();
		executor = Executors.newFixedThreadPool(nProc); 
		adder = new BinaryTreeAdderLimitedBuffer(nProc);
	}
	
	// TEST SIMILI ALLA PARTE 1 +  ALTRI TESTI SUL WORKSTEALING 
	
	@Test
	public void serialSumEmptyQueue() throws Exception {
		assertEquals(serialOnerousSum.call(),new Integer(0));
	}
	
	@Test
	public void serialSumSingleNode() throws Exception {
		serialBuffer.offer(singleNode);
		assertEquals(serialOnerousSum.call(),new Integer(1));
	}
	
	@Test
	public void concurrentSumSingleNode() throws Exception {
		dequeList.get(0).offer(singleNode);
		
		List<Callable<Integer>> tasks = new ArrayList<>(nProc);
		for(BlockingDeque<Node> deque : dequeList)
			tasks.add(new OnerousSumRunLimited(deque,nProc,counter,dequeList));
		
		// the execution order is controlled by the JVM Thread Pool
		List<Future<Integer>> futureResults = executor.invokeAll(tasks);
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
		serialBuffer.offer(simpleTree);
		assertEquals(serialOnerousSum.call(),new Integer(15));
	}
	
	@Test
	public void concurrentSumSimpleTree() throws Exception {
		dequeList.get(0).offer(simpleTree);
		
		List<Callable<Integer>> tasks = new ArrayList<>(nProc);
		for(BlockingDeque<Node> deque : dequeList)
			tasks.add(new OnerousSumRunLimited(deque,nProc,counter,dequeList));
		
		// the execution order is controlled by the JVM Thread Pool
		List<Future<Integer>> futureResults = executor.invokeAll(tasks);
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
		serialBuffer.offer(balancedOrderedTree);
		assertEquals(serialOnerousSum.call(),new Integer((31*32/2))); //la sommatoria da 1 a 31
	}
	
	@Test
	public void concurrentSumOrderedTree() throws Exception {
		dequeList.get(0).offer(balancedOrderedTree);
		
		List<Callable<Integer>> tasks = new ArrayList<>(nProc);
		for(BlockingDeque<Node> deque : dequeList)
			tasks.add(new OnerousSumRunLimited(deque,nProc,counter,dequeList));
		
		// the execution order is controlled by the JVM Thread Pool
		List<Future<Integer>> futureResults = executor.invokeAll(tasks);
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
		BlockingDeque<Node> deque = new LinkedBlockingDeque<>();
		List<BlockingDeque<Node>> list = new LinkedList<>();
		list.add(deque);
		f1 = executor.submit(new OnerousSumRunLimited(deque,2,counter,list));
		int sum = 0;
		
		Thread.sleep(50);
		deque.offer(balancedOrderedTree);
		Thread.sleep(50);
		// il thread sta in wait, quindi il buffer rimane pieno
		asleep = !deque.isEmpty();
		
		synchronized (this.counter) { this.counter.notify(); }
		Thread.sleep(50);
		awake = deque.isEmpty();
		
		// faccio terminare il thread
		synchronized (this.counter) { 
			this.counter.incrementAndGet();
			this.counter.notify(); 
			}
		sum += f1.get();
		executor.shutdown();
		
		assertTrue(asleep);
		assertTrue(awake);
		assertEquals(sum,31*32/2);
	}
	
	@Test
	public void theLastWakeEveryone() throws Exception {
		Future<Integer> f,f4;
		
		List<Future<Integer>> futureResults = new ArrayList<>(3);	
		List<BlockingDeque<Node>> list = new LinkedList<>();
		list.add(new LinkedBlockingDeque<>());
		list.add(new LinkedBlockingDeque<>());
		list.add(new LinkedBlockingDeque<>());
		for(BlockingDeque<Node> deque : list) {
			f = executor.submit(new OnerousSumRunLimited(deque,4,counter,list));
			futureResults.add(f);
		}
		
		Thread.sleep(50);
		BlockingDeque<Node> dek = new LinkedBlockingDeque<>();
		dek.offer(singleNode);
		list.add(dek);
		
		// il quarto thread, elabora l'unico nodo e sveglia gli altri  per terminare
		f4 = executor.submit(new OnerousSumRunLimited(dek,4,counter,list));
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
		
		List<BlockingDeque<Node>> list = new LinkedList<>();
		BlockingDeque<Node> dek = new LinkedBlockingDeque<>();
		dek.offer(singleNode);
		list.add(dek);
		//un nodo con un solo figlio
		dek.offer(new SimpleNode(1,new SimpleNode(1),null));

		Thread.sleep(50);
		executor.submit(new OnerousSumRunLimited(dek,2,counter,list));
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
	
	// TEST SUL WORK STEALING
	
	@Test
	public void workStealingBetweenTwoBuffers() {
		BlockingDeque<Node> buffer1 = dequeList.get(0);
		BlockingDeque<Node> buffer2 = dequeList.get(nProc-1);
		buffer2.offer(singleNode);
		OnerousSumRunLimited callable = new OnerousSumRunLimited(buffer1,nProc,null,dequeList);
		callable.workStealing();
		
		assertFalse(buffer1.isEmpty());
	}
	
	@Test
	public void aThreadStealWork() throws Exception {
		BlockingDeque<Node> buffer1 = dequeList.get(0);
		BlockingDeque<Node> buffer2 = dequeList.get(nProc-1);
		buffer1.offer(simpleTree);
		buffer2.offer(singleNode);
		
		Future<Integer> f = executor.submit(new OnerousSumRunLimited(buffer1,1,counter,dequeList));
		Thread.sleep(50);
		assertEquals((int)f.get(),16); //15 + 1(stolen)
		assertTrue(buffer2.isEmpty());
		
		executor.shutdown();
	}
	
	// due thread, uno col buffer molto pieno, l'altro gli ruba il lavoro in cima alla lista
	@Test
	public void concurrentThreadsStealOnTop() throws Exception {
		List<BlockingDeque<Node>> bufferList = new ArrayList<>(2);
		BlockingDeque<Node> buffer1 = new LinkedBlockingDeque<>();
		BlockingDeque<Node> buffer2 = new LinkedBlockingDeque<>();
		bufferList.add(buffer1);
		bufferList.add(buffer2);
		buffer1.offer(new SimpleNode(10000,null,null));
		buffer1.offer(TreeUtility.balancedTree(8));
		buffer2.offer(simpleTree); 
		/* mentre il 1 calcola sull'albero alto 8, il 2 calcola sul suo albero 
		 * semplice e poi ruba il nodo da 10000 che era in testa alla lista */
		
		executor.submit(new OnerousSumRunLimited(buffer1,2,counter,bufferList));
		Future<Integer> f = executor.submit(new OnerousSumRunLimited(buffer2,2,counter,bufferList));
		
		Thread.sleep(500);
		int result = f.get();
		assertTrue(result>= 10000);
		
		executor.shutdown();
	}
	
	
}
