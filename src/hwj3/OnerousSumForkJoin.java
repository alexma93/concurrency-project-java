package hwj3;
import java.util.concurrent.RecursiveTask;

import tree.FakeProcessor;
import tree.Node;

public class OnerousSumForkJoin extends RecursiveTask<Integer> {

	private static final long serialVersionUID = 1L;

	private Node root;

	public OnerousSumForkJoin(Node root) {
		this.root = root;
	}
	
	@Override
	public Integer compute() {
		int sum = 0;
		int partial;
		OnerousSumForkJoin taskSx = null;
		OnerousSumForkJoin taskDx = null;
		if(root.getSx()!=null) {
			taskSx = new OnerousSumForkJoin(root.getSx());
			taskSx.fork();
		}
		if(root.getDx()!=null) {
			taskDx = new OnerousSumForkJoin(root.getDx());
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
