package treeAdder;

public class simpleNode implements Node {
	private Node sx;
	private Node dx;
	private int value;

	public simpleNode(int val) {
		this.sx = null;
		this.dx = null;
		this.value = val;
	}
	
	public simpleNode(int val, Node sx, Node dx) {
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
