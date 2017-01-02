package hwj4;

import java.util.LinkedList;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

import treeAdder.Node;

public class BinaryTreeSpliterator implements Spliterator<Node> {
	private Node root; //nodo corrente, sul quale si prova a splittare
	private List<Node> list; //lista di nodi da elaborare

	public BinaryTreeSpliterator(Node n,List<Node> list) {
		this.root = n;
		this.list = list;
	}

	@Override
	public long estimateSize() {
		return Long.MAX_VALUE;
	}

	/* elaboro prima il nodo corrente, e poi tutti i nodi che avevo in lista
	 */
	@Override
	public boolean tryAdvance(Consumer<? super Node> action) {
		if(root!=null) {
			action.accept(root);
			if (list.size()!=0)
				this.root = this.list.remove(0);
			else this.root = null;
			return true;
		}
		return false;
	}

	/* Se il nodo corrente ha un solo figlio, non splitto, aggiungo il nodo in lista e passo al nodo successivo(il figlio).
	 * Se ha due figli, splitto in due parti: la parte corrente elaborera' i nodi che aveva gia' in lista,
	 * e provera' a splittare dal nodo sinistro; la nuova parte inserisce il nodo corrente in lista e provera'
	 * a splittare il nodo destro. esempio: ([1,2],corrente) => ([1,2],sinistro) ; ([corrente],destro)
	 * */
	@Override
	public Spliterator<Node> trySplit() {
		if(root.getSx()!=null && root.getDx()!=null) {
			Node d = root.getDx();
			Node sin = root.getSx();
			List<Node> newList = new LinkedList<>();
			newList.add(this.root);
			this.root = sin;
			return new BinaryTreeSpliterator(d,newList);
		}
		else if (root.getSx()!=null) {
			Node sin = root.getSx();
			this.list.add(this.root);
			this.root = sin;
		} else if (root.getDx()!=null) {
			Node d = root.getDx();
			this.list.add(this.root);
			this.root = d;
		}
		return null;
	}
	
	@Override
	public int characteristics() {
		return 0;
	}

	public Node getRoot() {
		return root;
	}

	public List<Node> getList() {
		return list;
	}
}
