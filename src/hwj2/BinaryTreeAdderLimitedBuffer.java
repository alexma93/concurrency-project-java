package hwj2;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import tree.*;

public class BinaryTreeAdderLimitedBuffer implements BinaryTreeAdder {
	private final int nProc;

	public BinaryTreeAdderLimitedBuffer(int n) {
		this.nProc = n;
	}

	@Override
	public int computeOnerousSum(Node root) {
		Future<Integer> f;
		AtomicInteger counter = new AtomicInteger();
		ExecutorService ex = Executors.newFixedThreadPool(this.nProc);
		List<BlockingDeque<Node>> lists = new ArrayList<>(this.nProc); //nessuna scrittura, non serve sincronizzarla
		for(int i=0;i<this.nProc;i++)
			lists.add(new LinkedBlockingDeque<>());
		
		lists.get(0).offer(root); //metto la radice nella prima deque
		
		List<Future<Integer>> futureResults = new ArrayList<>(this.nProc);

		
		for(BlockingDeque<Node> deque : lists) {
			f = ex.submit(new OnerousSumRunLimited(deque,this.nProc,counter,lists));
			futureResults.add(f);
		}
		int sum = 0;
		try {
			for(Future<Integer> fu: futureResults)
				sum += fu.get();
		} catch(Exception except){except.printStackTrace();	}
		ex.shutdown();
		return sum;
	}

}
