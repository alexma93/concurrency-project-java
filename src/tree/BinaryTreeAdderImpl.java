package tree;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class BinaryTreeAdderImpl implements BinaryTreeAdder {
	private final int nProc;

	public BinaryTreeAdderImpl(int n) {
		this.nProc = n;
	}

	@Override
	public int computeOnerousSum(Node root) {
		Future<Integer> f;
		AtomicInteger counter = new AtomicInteger();
		ExecutorService ex = Executors.newFixedThreadPool(this.nProc);
		LinkedBlockingQueue<Node> buffer = new LinkedBlockingQueue<>();
		buffer.offer(root);
		List<Future<Integer>> futureResults = new ArrayList<>(this.nProc);
		for(int i=0;i<this.nProc;i++) {
			f = ex.submit(new OnerousSumRun(buffer,this.nProc,counter));
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

	public int serialComputeOnerousSum(Node root) {
		int sum = 0;
		if(root!=null) {
			FakeProcessor proc = new FakeProcessor(2000);
			sum += proc.onerousFunction(root.getValue());
			sum += computeOnerousSum(root.getSx());
			sum += computeOnerousSum(root.getDx());
		}
		return sum;
	}

}
