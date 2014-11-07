package io.netty.example.netty3;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.LengthFieldPrepender;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;

public class Netty3ManyClientsRunner {

	static String HOST;
	static int FACTOR;
	static int CONN_NUM;
	static int CONN_RAMPS;
	static int RAMP_PAUSE;

	static String helloRequestTemplate = "{\"qualifier\":\"pt.openapi.hello/sayHello\",\"contextId\":\"[%CONTEXT_ID%]\",\"data\":{\"name\":\"ronen\"}}";

	public static void main(String[] args) throws InterruptedException {

		HOST = args[0];
		FACTOR = Integer.parseInt(args[1]);
		CONN_NUM = Integer.parseInt(args[2]);
		CONN_RAMPS = Integer.parseInt(args[3]);
        RAMP_PAUSE = Integer.parseInt(args[4]);

		ClientBootstrap clientBootstrap = new ClientBootstrap(
				new NioClientSocketChannelFactory(
						Executors.newCachedThreadPool(),
						Executors.newCachedThreadPool()));

		clientBootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			@Override
			public ChannelPipeline getPipeline() throws Exception {
				ChannelPipeline pipeline = Channels.pipeline();
				pipeline.addLast("prepender", new LengthFieldPrepender(4));
				pipeline.addLast("decoder", new StringDecoder());
				pipeline.addLast("framer", new LengthFieldBasedFrameDecoder(1024 * 2, 0, 4));
				pipeline.addLast("encoder", new StringEncoder());
				pipeline.addLast("handler", new ManyClientHandler());
				return pipeline;
			}
		});

		for (int r = 0; r < CONN_RAMPS; r++) {
			for (int i = 0; i < CONN_NUM; i++) {
				ChannelFuture future = clientBootstrap.connect(new InetSocketAddress(HOST, 4800));
				Channel channel = future.await().getChannel();
				ValueLatch<String> createContextLatch = new ValueLatch<>();
				channel.setAttachment(createContextLatch);

				channel.write("{\"qualifier\":\"pt.openapi.context/createContextRequest\"}");

				String contextId = createContextLatch.getValue(1, TimeUnit.SECONDS);
				if (contextId == null) {
					System.out.println("session not created! exiting test.");
					return;
				}

				System.out.println("session created: " + contextId);
				String helloRequest = helloRequestTemplate.replace("[%CONTEXT_ID%]", contextId);
				channel.setAttachment(helloRequest);

				channel.write(helloRequest);
			}
            TimeUnit.MILLISECONDS.sleep(RAMP_PAUSE);
		}

		Thread.currentThread().join();
	}
}
