package treeAdder;

public class SimpleNode implements Node {
	private Node sx;
	private Node dx;
	private int value;

	public SimpleNode(int val) {
		this.sx = null;
		this.dx = null;
		this.value = val;
	}
	
	public SimpleNode(int val, Node sx, Node dx) {
		this.sx = sx;
		this.dx = dx;
		this.value = val;
	}
	
	@Override
	public Node getSx() {
		return this.sx;
	}

	@Override
	public Node getDx() {
		return this.dx;
	}

	@Override
	public int getValue() {
		return this.value;
	}

}
