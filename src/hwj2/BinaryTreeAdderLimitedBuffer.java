package hwj2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

import treeAdder.*;

public class BinaryTreeAdderLimitedBuffer implements BinaryTreeAdder {
	private final int nProc;

	public BinaryTreeAdderLimitedBuffer(int n) {
		this.nProc = n;
	}

	@Override
	public int computeOnerousSum(Node root) {
		AtomicInteger counter = new AtomicInteger();
		ExecutorService ex = Executors.newFixedThreadPool(this.nProc);
		
		// ogni worker thread ha una propria deque
		// non serve una lista sincronizzata, non ho nessuna scrittura
		List<BlockingDeque<Node>> dequeList = new ArrayList<>(this.nProc);
		for(int i=0;i<this.nProc;i++)
			dequeList.add(new LinkedBlockingDeque<>());
		
		dequeList.get(0).offer(root); //metto la radice nella prima deque
		
		List<Callable<Integer>> tasks = new ArrayList<>(this.nProc);
		for(BlockingDeque<Node> deque : dequeList)
			tasks.add(new OnerousSumRunLimited(deque,this.nProc,counter,dequeList));
		int sum = 0;
		try {
			List<Future<Integer>> futureResults = ex.invokeAll(tasks);
			for(Future<Integer> fu: futureResults)
				sum += fu.get();
		} catch(Exception except){except.printStackTrace();	}
		ex.shutdown();
		return sum;
	}

}
