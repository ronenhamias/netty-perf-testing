package io.netty.example.common;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Counter {
	static AtomicInteger i = new AtomicInteger(0);

	static ScheduledExecutorService service = Executors.newScheduledThreadPool(1);

	static LimitedQueue<Integer> queue = new LimitedQueue<Integer>(10);

	public static void add() {
		i.addAndGet(1);
	}

	public static int get() {
		return i.get();
	}

	public static void start() {
		service.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				queue.add(Counter.get());
				if (queue.size() == 10) {
					Object[] array = queue.toArray();

					Integer first = (Integer) array[0];
					Integer last = (Integer) array[9];
					if (first != 0 && last != 0) {
						Integer avg = ((last - first) / 10);
						System.out.println("Requests per second: " + avg);
					}
				}
			}
		}, 1, 1, TimeUnit.SECONDS);
	}
}
