package io.netty.example.common;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KPIReporter {

	private static ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TPSCounter.class);
	
	private static final CPUCounter cpuCounter = new CPUCounter(10);
	
	public static final TPSCounter tpsCounter = new TPSCounter(10);
	
	public static void start() {
		service.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					cpuCounter.add();
					tpsCounter.add();
					
					Double CPU = cpuCounter.getMovingAvg();
					Double TPS = tpsCounter.getMovingAvg();
					
					System.out.println("TPS:" + TPS  + "|CPU:" + CPU );
					LOGGER.info(TPS.toString() + "," + CPU + ",");
				} catch (Exception e) {
				}
			}
		}, 1, 1, TimeUnit.SECONDS);
	}
}
