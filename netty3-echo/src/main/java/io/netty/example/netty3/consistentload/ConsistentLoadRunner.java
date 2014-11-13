package io.netty.example.netty3.consistentload;

import io.netty.example.netty3.ValueLatch;

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

public class ConsistentLoadRunner {

	static String HOST;
	static int FACTOR;
	static int CONN_NUM;
	static int CONN_RAMPS;
	static int RAMP_PAUSE;

	static String helloRequestTemplate = "{\"qualifier\":\"pt.openapi.hello/sayHello\",\"contextId\":\"[%CONTEXT_ID%]\",\"data\":{\"name\":\"ronen\"}}";

	public static void main(String[] args) throws InterruptedException {
		if(args.length >0)
			HOST = args[0];
		else{
			HOST = "localhost";
		}
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
				pipeline.addLast("handler", new ConsistentLoadHandler());
				return pipeline;
			}
		});

		UserSenario senario = new UserSenario(clientBootstrap,HOST, 4800);
		String contextId = senario.setUp(10);
		
		senario.start();
		
		Thread.currentThread().join();
	}
}
