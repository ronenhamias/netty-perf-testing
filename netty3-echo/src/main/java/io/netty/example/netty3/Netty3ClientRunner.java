package io.netty.example.netty3;

import java.util.concurrent.TimeUnit;

import org.jboss.netty.channel.Channel;

public class Netty3ClientRunner {

	private static final String HOST = "127.0.0.1";
	private static final int PORT = 4800;

	static String createContextRequest = "{\"qualifier\":\"pt.openapi.context/createContextRequest\"}";

	static String helloRequestTemplate = "{\"qualifier\":\"pt.openapi.hello/sayHello\",\"contextId\":\"[%CONTEXT_ID%]\",\"data\":{\"name\":\"ronen\"}}";

	public static ValueLatch<String> latch = new ValueLatch<>();
	private static String helloRequest = null;
	
	public static void main(String[] args) throws InterruptedException {
		ClientFactory client = new ClientFactory();
		Channel channel = client.connect(HOST, PORT);

		channel.write(createContextRequest);

		String contextId = latch.getValue(10,TimeUnit.SECONDS);
		if(contextId==null)
			return;
		
	    helloRequest = helloRequestTemplate.replace("[%CONTEXT_ID%]", contextId);
		
	    channel.write(helloRequest);
		
		Thread.sleep(Integer.MAX_VALUE);
	}
	public static void send(Channel channel) {
		channel.write(helloRequest);
	}
	
}
