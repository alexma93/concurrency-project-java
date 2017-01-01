package hwj4;

import java.util.LinkedList;
import java.util.Spliterator;
import java.util.concurrent.LinkedBlockingQueue;

import tree.*;

public class ListTree extends LinkedList<Node> {

	private static final long serialVersionUID = 1L;
	private Node root;
	
	public ListTree(Node r) {
		this.root = r;
	}
	
	
	@Override
	public Spliterator<Node> spliterator() {
		return new BinaryTreeSpliterator(this.root,this);
	}
}
