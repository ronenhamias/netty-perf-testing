package io.netty.example.netty3.consistentload;

import io.netty.example.netty3.ValueLatch;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;

public class UserSenario implements Runnable{

	private ClientBootstrap clientBootstrap;
	private String host;
	private int port;

	private String contextId;
	private Channel channel;
	
	private String messageTemplate;
	private String helloRequest;
	Random rnd = new Random();
	public UserSenario(ClientBootstrap clientBootstrap,String host, int port) {
		this.clientBootstrap = clientBootstrap;
		this.host = host;
		this.port = port; 
	}

	ExecutorService executor =  Executors.newSingleThreadExecutor();
	
	@Override
	public void run() {
		for (int x = 0; x < Integer.MAX_VALUE; x++) {
			channel.write(helloRequest);
			try {
				TimeUnit.MILLISECONDS.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public String setUp(int timeout) throws InterruptedException {
		ChannelFuture future = clientBootstrap.connect(new InetSocketAddress(host, port));
		channel = future.await().getChannel();
		ValueLatch<String> createContextLatch = new ValueLatch<>();
		channel.setAttachment(createContextLatch);
		channel.write("{\"qualifier\":\"pt.openapi.context/createContextRequest\"}");
		contextId = createContextLatch.getValue(timeout, TimeUnit.SECONDS);
	
		if (contextId == null) {
			System.out.println("session not created! try again test.");
			return null;
		}
		System.out.println("session created: " + contextId);
		
		return contextId;
	}

	public void start() {
		String helloRequestTemplate = "{\"qualifier\":\"pt.openapi.hello/sayHello\",\"contextId\":\"[%CONTEXT_ID%]\",\"data\":{\"name\":\"ronen\"}}";
		helloRequest = helloRequestTemplate.replace("[%CONTEXT_ID%]", contextId);
		channel.setAttachment(helloRequest);
		executor.execute(this);
	}
}
