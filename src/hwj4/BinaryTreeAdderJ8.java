package hwj4;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import tree.BinaryTreeAdder;
import tree.FakeProcessor;
import tree.Node;

public class BinaryTreeAdderJ8 implements BinaryTreeAdder {

	@Override
	public int computeOnerousSum(Node root) {
		
		//TODO: vedere se e' meglio i completableFuture
		//TODO: devo splittare root
		List<Integer> results = new ListTree(root).parallelStream().map(n -> 
			new FakeProcessor(2000).onerousFunction(n.getValue())).collect(Collectors.toList());
		int sum = 0;
		for(Integer i: results)
			sum += i;
		return sum;
	}

}
