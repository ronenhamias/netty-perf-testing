package io.netty.example.common;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class KPIReporter {

	private static ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
	
	private static final CPUCounter cpuCounter = new CPUCounter(10);
	
	public static final TPSCounter tpsCounter = new TPSCounter(10);
	
	public static void start() {
		service.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					cpuCounter.add();
					tpsCounter.add();
					
					Integer CPU = cpuCounter.getMovingAvg().intValue();
					Integer TPS = tpsCounter.getMovingAvg().intValue();
					
					System.out.println("TPS:" + TPS  + "|CPU:" + CPU );
				} catch (Exception e) {
				}
			}
		}, 1, 1, TimeUnit.SECONDS);
	}
}
