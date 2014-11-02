package io.netty.example.common;

import java.util.concurrent.atomic.AtomicInteger;

public class TPSCounter extends AbstractCounter{
	public TPSCounter(int maxQueueSize) {
		super(maxQueueSize);
	}

	AtomicInteger i = new AtomicInteger(0);
	
	public void add() {
		i.addAndGet(1);
	}

	@Override
	public Double getMovingAvg(){
		super.add(i.doubleValue());
		if (queue.size() == maxQueueSize) {
			Object[] array = queue.toArray();
			Double first = (Double) array[0];
			Double last = (Double) array[maxQueueSize-1];
			if (first != 0 && last != 0) {
				Double avg = ((last - first) / maxQueueSize);
				return avg;
			}
		}
		return Double.NaN;	
	}	
}
