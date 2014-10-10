package io.netty.example.common;

import java.lang.management.ManagementFactory;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Counter {
	private static final Logger LOGGER = LoggerFactory.getLogger(Counter.class);

	static AtomicInteger i = new AtomicInteger(0);

	static ScheduledExecutorService service = Executors.newScheduledThreadPool(1);

	static LimitedQueue<Integer> queue = new LimitedQueue<Integer>(10);

	public static void add() {
		i.addAndGet(1);
	}

	public static int get() {
		return i.get();
	}

	public static double getProcessCpuLoad() throws MalformedObjectNameException, ReflectionException, InstanceNotFoundException {

		MBeanServer mbs    = ManagementFactory.getPlatformMBeanServer();
		ObjectName name    = ObjectName.getInstance("java.lang:type=OperatingSystem");
		AttributeList list = mbs.getAttributes(name, new String[]{ "ProcessCpuLoad" });

		if (list.isEmpty())     return Double.NaN;

		Attribute att = (Attribute)list.get(0);
		Double value  = (Double)att.getValue();

		if (value == -1.0)      return Double.NaN;  // usually takes a couple of seconds before we get real values

		return ((int)(value * 1000) / 10.0);        // returns a percentage value with 1 decimal point precision
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
						try {
							double CPU = getProcessCpuLoad();
							System.out.println("TPS:" + avg + "|CPU:" + CPU );
							LOGGER.info(avg.toString() + "," + CPU + ",");
						} catch (Exception e) {
						}
					}
				}
			}
		}, 1, 1, TimeUnit.SECONDS);
	}
}
