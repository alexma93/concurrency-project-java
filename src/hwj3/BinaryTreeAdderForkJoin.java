package hwj3;
import java.util.concurrent.ForkJoinPool;

import tree.BinaryTreeAdder;
import tree.Node;

public class BinaryTreeAdderForkJoin implements BinaryTreeAdder {

	@Override
	public int computeOnerousSum(Node root) {
		final ForkJoinPool pool = new ForkJoinPool();
		
		int sum = 0;
		try {
			sum = pool.invoke(new OnerousSumForkJoin(root));
		} finally {pool.shutdown();}
		return sum;
	}

}
