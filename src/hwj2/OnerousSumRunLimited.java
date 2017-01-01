package hwj2;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import treeAdder.*;

public class OnerousSumRunLimited implements Callable<Integer> {

	private BlockingDeque<Node> buffer;
	//per gestire che concludono tutti, come in hwj1
	private int numProc; 
	private AtomicInteger counter;
	// lista contenente tutte le deque
	private List<BlockingDeque<Node>> dequeList;

	public OnerousSumRunLimited(BlockingDeque<Node>buffer,int n,AtomicInteger c,List<BlockingDeque<Node>> dequeList) {
		this.buffer = buffer;
		this.numProc = n;
		this.counter = c;
		this.dequeList = dequeList;
	}


	@Override
	public Integer call() throws Exception {
		int sum = 0;
		boolean incremented = false; // se ho aumentato il contatore

		//finche' non hanno concluso tutti i worker thread
		while(this.counter.get()<this.numProc) {
			if(incremented) {
				incremented = false;
				this.counter.decrementAndGet();
			}
			if(!this.buffer.isEmpty()) {
				Node root = this.buffer.pollLast(); //ritorna null se e' vuota
				sum += depthSum(root);
			}
			// provo a rubare del lavoro dagli altri
			else if (!workStealing())
				// se non ci sono riuscito e gli altri buffer erano vuoti
				synchronized (this.counter) {
					this.counter.incrementAndGet();
					// se abbiamo tutti terminato, lo segnalo a tutti e terminiamo
					if(this.counter.get()==this.numProc)
						this.counter.notifyAll();
					// altrimenti mi metto in attesa o che un buffer si riempia o che tutti terminino
					else {
						incremented = true;
						this.counter.wait();
					}
				}
		}
		return sum;
	}

	/* calcola una somma a partire da un nodo root:
	 * se il nodo ha entrambi i figli, scendo verso sinistra e inserisco il destro nel buffer
	 * se ne ha solo uno, lo inserisco nel buffer e elaboro il nodo root.
	 */
	public int depthSum(Node root) {
		int sum = 0;
		if(root!=null) {
			if(root.getSx()!=null)
				if(root.getDx()!=null) {
					this.buffer.offer(root.getDx());
					// segnalo che il buffer non e' piu' vuoto
					synchronized (this.counter) {counter.notify();}
					sum += depthSum(root.getSx());
				}
				else {
					this.buffer.offer(root.getSx());
					synchronized (this.counter) {counter.notify();}
				}
			else if(root.getDx()!=null) {
				this.buffer.offer(root.getDx());
				synchronized (this.counter) {counter.notify();}
			}
			FakeProcessor proc = new FakeProcessor(2000);
			sum += proc.onerousFunction(root.getValue());
		}
		return sum;
	}

	// true se e' riuscito a rubare da un'altra deque
	public boolean workStealing() {
		for (BlockingDeque<Node> dek : this.dequeList)
			if(dek!=this.buffer && !dek.isEmpty()) {
				Node n = dek.pollFirst();
				if(n!=null) {
					this.buffer.offer(n);
					return true;
				}
			}
		return false;
	}

}
