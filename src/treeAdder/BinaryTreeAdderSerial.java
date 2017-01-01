package treeAdder;
import treeAdder.BinaryTreeAdder;
import treeAdder.FakeProcessor;
import treeAdder.Node;

/*
 * Versione seriale dell'adder
 */
public class BinaryTreeAdderSerial implements BinaryTreeAdder {

	@Override
	public int computeOnerousSum(Node root) {
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
