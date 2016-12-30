package hwj2;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import tree.*;

public class OnerousSumRunLimited implements Callable<Integer> {

	private BlockingDeque<Node> buffer;
	private int numProc; //per gestire che concludono tutti
	private AtomicInteger counter;
	private List<BlockingDeque<Node>> list;
	private int quanti; //TODO

	public OnerousSumRunLimited(BlockingDeque<Node>buffer,int n,AtomicInteger c,List<BlockingDeque<Node>> list) {
		this.buffer = buffer;
		this.numProc = n;
		this.counter = c;
		this.list = list;
		quanti = 0;
	}

	//TODO: probabile devo togliere le wait e notify. servono per svegliare i thread quando in realta'  
	// non e' vuoto. i thread devono dormire perche' devo evitare attese attive
	//TODO: ogni tanto si blocca. i break sono orribili
	@Override
	public Integer call() throws Exception {
		int sum = 0;
		boolean notEmpty = false;
		boolean incremented = false;
		while(this.counter.get()<this.numProc) {
			notEmpty = false;
			if(!this.buffer.isEmpty()) {
				if(incremented) {
					incremented = false;
					this.counter.decrementAndGet();
				}
				Node root = this.buffer.poll(); //ritorna null se e' vuota
				sum += depthSum(root);

			}
			else {
				while (!notEmpty) {
					if (otherBuffersEmpty()) {
						synchronized (this.counter) {
							this.counter.incrementAndGet();
							if(this.counter.get()==this.numProc) {
								this.counter.notifyAll();
								return sum;
							} else {
								incremented = true;
								this.counter.wait();
								break;
							}
						}
					}
					else { //work stealing
						notEmpty = workStealing();
					}
				}

			}
		}
		return sum;
	}

	public int depthSum(Node root) {
		int sum = 0;
		if(root!=null) {
			if(root.getSx()!=null) {
				if(root.getDx()!=null) {
					this.buffer.offer(root.getDx());
					synchronized (this.counter) {counter.notify();}
					sum += depthSum(root.getSx());
				}
				else {
					this.buffer.offer(root.getSx());
					synchronized (this.counter) {counter.notify();}
				}
			}
			else if(root.getDx()!=null) {
				sum += depthSum(root.getDx());
			}
			FakeProcessor proc = new FakeProcessor(2000);
			sum += proc.onerousFunction(root.getValue());
			quanti++;
		}
		return sum;
	}


	public boolean otherBuffersEmpty() {
		for (BlockingDeque<Node> dek : this.list)
			if(dek!=this.buffer && !dek.isEmpty())
				return false;
		return true;
	}

	public boolean workStealing() {
		for (BlockingDeque<Node> dek : this.list)
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
