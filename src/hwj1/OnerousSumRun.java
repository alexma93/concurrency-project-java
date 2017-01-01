package hwj1;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import treeAdder.FakeProcessor;
import treeAdder.Node;

public class OnerousSumRun implements Callable<Integer> {

	private BlockingQueue<Node> buffer;
	// so che tutti i thread hanno concluso, quando counter = numProc. counter e' condiviso
	private int numProc; 
	private AtomicInteger counter;

	public OnerousSumRun(BlockingQueue<Node> buff,int n,AtomicInteger c) {
		this.buffer = buff;
		this.numProc = n;
		this.counter = c;
	}

	//TODO: probabile devo togliere le wait e notify. servono per svegliare i thread quando in realta'  
	// non e' vuoto. i thread devono dormire perche' devo evitare attese attive
	@Override
	public Integer call() throws Exception {
		int sum = 0;
		boolean incremented = false;
		while(this.counter.get()<this.numProc) {
			// se credevo di aver finito, ma mi hanno svegliato e non abbiamo finito tutti, diminuisco il contatore
			if(incremented) {
				incremented = false;
				this.counter.decrementAndGet();
			}
			if(!this.buffer.isEmpty()) {
				Node root = this.buffer.poll(); //ritorna null se e' vuoto
				if(root!=null) {
					if(root.getSx()!=null) {
						this.buffer.offer(root.getSx());
						// segnalo che il buffer non e' piu' vuoto
						synchronized (this.counter) {this.counter.notify();}
					}
					if(root.getDx()!=null) {
						this.buffer.offer(root.getDx());
						synchronized (this.counter) {this.counter.notify();}
					}
					FakeProcessor proc = new FakeProcessor(2000);
					sum += proc.onerousFunction(root.getValue());
				}
			}
			/* se il buffer e' vuoto, e tutti sono in attesa perche' lo hanno visto vuoto, li sveglio tutti  e terminiamo.
			 * altrimenti mi metto in attesa che il buffer si riempia o che tutti abbiano terminato */
			else synchronized (this.counter) {
				this.counter.incrementAndGet();
				if(this.counter.get()==this.numProc)
					this.counter.notifyAll();
				else {
					incremented = true;
					this.counter.wait();
				}
			}
		}
		return sum;
	}

}
