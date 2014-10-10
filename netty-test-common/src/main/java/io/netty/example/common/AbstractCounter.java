package io.netty.example.common;

public abstract class AbstractCounter  implements ICounter{
	
	protected LimitedQueue<Double> queue = null;

	protected int maxQueueSize;
	
	public AbstractCounter ( int maxQueueSize){
		this.maxQueueSize = maxQueueSize;
		this.queue = new LimitedQueue<Double>(maxQueueSize);
	}
	
	@Override
	public void add(Double o) {
		this.queue.add(o);
	}
	
}
