package hwj4;
import java.util.concurrent.ForkJoinPool;

import tree.BinaryTreeAdder;
import tree.Node;

public class BinaryTreeAdderJ8 implements BinaryTreeAdder {

	@Override
	public int computeOnerousSum(Node root) {
		final ForkJoinPool pool = new ForkJoinPool();
		
		int sum = 0;
		try {
			sum = pool.invoke(new OnerousSumJ8(root));
		} finally {pool.shutdown();}
		return sum;
	}

}
