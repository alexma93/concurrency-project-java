package hwj1;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import treeAdder.BinaryTreeAdder;
import treeAdder.Node;

public class BinaryTreeAdderImpl implements BinaryTreeAdder {
	private final int nProc;

	public BinaryTreeAdderImpl(int n) {
		this.nProc = n;
	}

	@Override
	public int computeOnerousSum(Node root) {
		AtomicInteger counter = new AtomicInteger();
		
		ExecutorService ex = Executors.newFixedThreadPool(this.nProc);
		
		LinkedBlockingQueue<Node> buffer = new LinkedBlockingQueue<>();
		buffer.offer(root);
		
		List<Callable<Integer>> tasks = new ArrayList<>(this.nProc);
		for(int i=0; i<this.nProc; i++)
			tasks.add(new OnerousSumRun(buffer,nProc,counter));
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
