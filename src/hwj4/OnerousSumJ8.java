package hwj4;
import java.util.concurrent.RecursiveTask;

import tree.FakeProcessor;
import tree.Node;

public class OnerousSumJ8 extends RecursiveTask<Integer> {

	private static final long serialVersionUID = 1L;

	private Node root;

	public OnerousSumJ8(Node root) {
		this.root = root;
	}
	
	@Override
	public Integer compute() {
		int sum = 0;
		int partial;
		OnerousSumJ8 taskSx = null;
		OnerousSumJ8 taskDx = null;
		if(root.getSx()!=null) {
			taskSx = new OnerousSumJ8(root.getSx());
			taskSx.fork();
		}
		if(root.getDx()!=null) {
			taskDx = new OnerousSumJ8(root.getDx());
			taskDx.fork();
		}
		FakeProcessor proc = new FakeProcessor(2000);
		partial = proc.onerousFunction(root.getValue());
		sum += partial;
		if(taskSx!=null)
			sum += taskSx.join();
		if(taskDx!=null)
			sum += taskDx.join();
		return sum;
	}

}
