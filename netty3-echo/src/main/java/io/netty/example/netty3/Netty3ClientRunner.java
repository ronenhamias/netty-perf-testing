package io.netty.example.netty3;

import java.util.concurrent.TimeUnit;

import org.jboss.netty.channel.Channel;

public class Netty3ClientRunner {

	private static String HOST = "127.0.0.1";
	private static final int PORT = 4800;
	public static  String POOLSIZE = "10";

	static String createContextRequest = "{\"qualifier\":\"pt.openapi.context/createContextRequest\"}";

	static String helloRequestTemplate = "{\"qualifier\":\"pt.openapi.hello/sayHello\",\"contextId\":\"[%CONTEXT_ID%]\",\"data\":{\"name\":\"ronen\"}}";

	public static ValueLatch<String> latch = new ValueLatch<>();
	private static String helloRequest = null;
	public static String FACTOR = "2";
	
	public static void main(String[] args) throws InterruptedException {

		System.out.println("starting perf test");
		
		if(args[0]!=null)
			HOST = args[0];
		if(args[1]!=null)
			FACTOR  = args[1];
		if(args[2]!=null)
			POOLSIZE  = args[2];
		
		System.out.println("connecting to: " + HOST);
		
		ClientFactory client = new ClientFactory();
		Channel channel = client.connect(args[0], PORT);
		System.out.println("create session: " + HOST + " open:" + channel.isOpen());
		channel.write(createContextRequest);

		String contextId = latch.getValue(10,TimeUnit.SECONDS);
		if(contextId==null)
			return;
		System.out.println("session created: " + contextId);
	    helloRequest = helloRequestTemplate.replace("[%CONTEXT_ID%]", contextId);
		
	    System.out.println("sending first message");
	    channel.write(helloRequest);
		
		Thread.sleep(Integer.MAX_VALUE);
	}
	public static void send(Channel channel) {
		channel.write(helloRequest);
		
	}
	
}
