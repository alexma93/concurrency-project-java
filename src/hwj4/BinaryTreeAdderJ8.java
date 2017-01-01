package hwj4;
import java.util.List;
import java.util.stream.Collectors;

import treeAdder.BinaryTreeAdder;
import treeAdder.FakeProcessor;
import treeAdder.Node;

public class BinaryTreeAdderJ8 implements BinaryTreeAdder {

	@Override
	public int computeOnerousSum(Node root) {
		
		List<Integer> results = new ListTree(root).parallelStream().map(n -> 
			new FakeProcessor(2000).onerousFunction(n.getValue())).collect(Collectors.toList());
		int sum = 0;
		for(Integer i: results)
			sum += i;
		return sum;
	}

}
