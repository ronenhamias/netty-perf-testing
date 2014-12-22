package io.netty.example.netty3;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import joptsimple.BuiltinHelpFormatter;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.LengthFieldPrepender;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;

public class Netty3ManyClientsRunner {

	static final String ADDRESS = "address";
	static final String FACTOR = "factor";
	static final String CONN_NUM = "connNum";
	static final String RAMP_PAUSE = "rampPause";

	static int factor;
	static int connNum;
	static int rampPause;

	public static void main(String[] args) throws Exception {
		OptionParser parser = new OptionParser();
		parser.allowsUnrecognizedOptions();
		parser.formatHelpWith(new BuiltinHelpFormatter(1024, 8));
		parser.accepts(ADDRESS, "host:port").withRequiredArg().required();
		parser.accepts(FACTOR, "how many replies per request").withRequiredArg().required().ofType(Integer.class);
		parser.accepts(CONN_NUM, "total number of connections").withRequiredArg().required().ofType(Integer.class);
		parser.accepts(RAMP_PAUSE, "delay(ms) between connect attepmts").withRequiredArg().required().ofType(Integer.class);
		parser.acceptsAll(Arrays.asList("?"), "show help").forHelp();

		OptionSet opts = null;
		try {
			opts = parser.parse(args);
		} catch (Exception e) {
			System.out.println("\n" + e.getMessage() + "\n");
			parser.printHelpOn(System.out);
			System.exit(1);
		}
		if (opts.has("?")) {
			parser.printHelpOn(System.out);
			System.exit(1);
		}

		String address = (String) opts.valueOf(ADDRESS);
		String host = address.split(":")[0];
		int port = Integer.parseInt(address.split(":")[1]);

		factor = (int) opts.valueOf(FACTOR);
		connNum = (int) opts.valueOf(CONN_NUM);
		rampPause = (int) opts.valueOf(RAMP_PAUSE);

		ClientBootstrap clientBootstrap = new ClientBootstrap(
				new NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));

		clientBootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			@Override
			public ChannelPipeline getPipeline() throws Exception {
				ChannelPipeline pipeline = Channels.pipeline();
				pipeline.addLast("prepender", new LengthFieldPrepender(4));
				pipeline.addLast("decoder", new StringDecoder());
				pipeline.addLast("framer", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
				pipeline.addLast("encoder", new StringEncoder());
				pipeline.addLast("handler", new ManyClientHandler());
				return pipeline;
			}
		});

		for (int i = 0; i < connNum; i++) {
			ChannelFuture future = clientBootstrap.connect(new InetSocketAddress(host, port));
			Channel channel = future.await().getChannel();
			ValueLatch<String> createContextLatch = new ValueLatch<>();
			channel.setAttachment(createContextLatch);

			channel.write("{\"qualifier\":\"pt.openapi.context/createContextRequest\"}");
			String contextId = createContextLatch.getValue(10, TimeUnit.SECONDS);
			if (contextId == null) {
				System.out.println("!!! session not created, exiting test.");
				System.exit(1);
			}
			System.out.println("### session created: " + contextId);
			TimeUnit.MILLISECONDS.sleep(rampPause);
		}

		Thread.currentThread().join();
	}
}
