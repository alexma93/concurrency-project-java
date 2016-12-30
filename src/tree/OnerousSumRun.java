package tree;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

public class OnerousSumRun implements Callable<Integer> {

	private BlockingQueue<Node> buffer;
	private int numProc; //TODO: gestire che concludono tutti
	private AtomicInteger counter;
	private int quanti; //TODO: toglierlo

	public OnerousSumRun(BlockingQueue<Node> buff,int n,AtomicInteger c) {
		this.buffer = buff;
		this.numProc = n;
		this.counter = c;
		quanti = 0;
	}

	//TODO: probabile devo togliere le wait e notify. servono per svegliare i thread quando in realta'  
	// non e' vuoto. i thread devono dormire perche' devo evitare attese attive
	@Override
	public Integer call() throws Exception {
		int sum = 0;
		boolean incremented = false;
		while(this.counter.get()<this.numProc) {
			if(!this.buffer.isEmpty()) {
				if(incremented) {
					incremented = false;
					this.counter.decrementAndGet();
				}
				Node root = this.buffer.poll(); //ritorna null se e' vuota
				if(root!=null) {
					if(root.getSx()!=null) {
						this.buffer.offer(root.getSx());
						synchronized (this.counter) {this.counter.notify();}
					}
					if(root.getDx()!=null) {
						this.buffer.offer(root.getDx());
						synchronized (this.counter) {this.counter.notify();}
					}
					FakeProcessor proc = new FakeProcessor(2000);
					sum += proc.onerousFunction(root.getValue());
					quanti++;
				}
			}
			else {
				synchronized (this.counter) {
					this.counter.incrementAndGet();
					if(this.counter.get()==this.numProc) {
						this.counter.notifyAll();
						break;
					}
					incremented = true;
					this.counter.wait();
				}
			}
		}
		return sum;
	}

}
