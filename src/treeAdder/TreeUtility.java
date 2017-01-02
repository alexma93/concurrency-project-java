package treeAdder;

/*
 * classe per creare alberi
 */
public class TreeUtility {

	// albero binario bilanciato data l'altezza
	public static Node balancedTree(int height) {
		Node root = null;
		if(height>=0) {
			Node sx,dx;
			sx = balancedTree(height-1);
			dx = balancedTree(height-1);
			root = new SimpleNode(1,sx,dx);
		}
		return root;
	}
	
	//albero sbilanciato verso sinistra
	public static Node unbalancedLeftTree(int height) {
		Node root = null;
		if(height>=0) {
			Node sx,dx;
			sx = unbalancedLeftTree(height-1);
			dx = unbalancedLeftTree(height-2);
			root = new SimpleNode(1,sx,dx);
		}
		return root;
	}
	
	// albero sbilanciato verso destra
	public static Node unbalancedRightTree(int height) {
		Node root = null;
		if(height>=0) {
			Node sx,dx;
			sx = unbalancedRightTree(height-2);
			dx = unbalancedRightTree(height-1);
			root = new SimpleNode(1,sx,dx);
		}
		return root;
	}
	
	// creo un albero con valori nei nodi da 1 in poi
	public static Node balancedOrderedTree(int height) {
		return bODSupport(height,new int[]{1});
	}
	
	private static Node bODSupport(int height, int[] val) {
		Node root = null;
		if(height>=0) {
			Node sx,dx;
			int v = val[0];
			val[0]++;
			sx = bODSupport(height-1,val);
			dx = bODSupport(height-1,val);
			root = new SimpleNode(v,sx,dx);
		}
		return root;
	}

	/* public static void printTree(Node n) {
		if(n!=null) {
			System.out.print(" ["+n.getValue());
			if(n.getSx()!=null) {
				System.out.print(", ");
				printTree(n.getSx());
			}
			if(n.getDx()!=null) {
				System.out.print(", ");
				printTree(n.getDx());
			}
			System.out.print("]");
		}
	} */
}
