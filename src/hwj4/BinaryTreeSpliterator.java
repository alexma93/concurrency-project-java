package hwj4;

import java.util.Spliterator;
import java.util.function.Consumer;

import tree.Node;

public class BinaryTreeSpliterator implements Spliterator<Node> {
	private Node root;
	
	public BinaryTreeSpliterator(Node n) {
		this.root = n;
	}

	@Override
	public long estimateSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean tryAdvance(Consumer<? super Node> arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Spliterator<Node> trySplit() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int characteristics() {
		return 0;
	}

}
