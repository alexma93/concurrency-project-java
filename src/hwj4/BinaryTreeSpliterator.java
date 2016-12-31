package hwj4;

import java.util.List;
import java.util.Spliterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

import tree.Node;

public class BinaryTreeSpliterator implements Spliterator<Node> {
	private Node root;
	private BlockingQueue<Node> list;
	
	public BinaryTreeSpliterator(Node n,BlockingQueue<Node> list) {
		this.root = n;
		this.list = list;
	}

	@Override
	public long estimateSize() {
//		Node last = this.list.get(this.list.size()-1);
//		if (last.getSx()==null && last.getDx()==null)
//			return 0;
		return Long.MAX_VALUE;
	}

	@Override
	public boolean tryAdvance(Consumer<? super Node> action) {
		if(root!=null) {
			action.accept(root);
			this.list.remove(root);
			if (list.size()!=0)
				this.root = this.list.peek();
			else this.root = null;
			return true;
		}
		return false;
	}

	@Override
	public Spliterator<Node> trySplit() {
		if(root.getSx()!=null && root.getDx()!=null) {
			Node d = root.getDx();
			Node sin = root.getSx();
			this.list.add(sin);
			//this.list.remove(this.root); //non mi calcola i nodi interni perche' qui li rimuovo
			this.root = sin;
			BlockingQueue<Node> queue = new LinkedBlockingQueue<>();
			queue.add(d);
			return new BinaryTreeSpliterator(d,queue);
		}
		return null;
	}
	
	@Override
	public int characteristics() {
		return 0;
	}

}
