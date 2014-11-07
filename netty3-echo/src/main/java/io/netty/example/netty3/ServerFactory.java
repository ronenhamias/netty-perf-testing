package io.netty.example.netty3;

import org.jboss.netty.bootstrap.Bootstrap;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.buffer.HeapChannelBufferFactory;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.omg.CORBA.Environment;

import java.net.InetSocketAddress;
import java.nio.ByteOrder;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ServerFactory {

	public void bind(int port) {
		ServerBootstrap bootstrap =  initBootstrap();	   
		DefaultChannelGroup channelGroup = new DefaultChannelGroup("-all-channels");
		initAcceptor(bootstrap, channelGroup, port);
	}

	private ServerBootstrap initBootstrap() {
		ServerBootstrap serverBootstrap;
		Executor bossExecutor = Executors.newFixedThreadPool(8);
		Executor workerExecutor = Executors.newFixedThreadPool(8);
		ChannelFactory channelFactory = new NioServerSocketChannelFactory(bossExecutor, workerExecutor);
		serverBootstrap = new ServerBootstrap(channelFactory);
		initOptions(serverBootstrap);
		serverBootstrap.setPipelineFactory(new ServerPipelineFactory());
		return serverBootstrap;
	}

	private void initOptions(Bootstrap bootstrap){
		bootstrap.setOption("child.bufferFactory", HeapChannelBufferFactory.getInstance(ByteOrder.LITTLE_ENDIAN));
		bootstrap.setOption("child.tcpNoDelay", true);
		bootstrap.setOption("child.keepAlive", true);
		bootstrap.setOption("child.reuseAddress", true);
		bootstrap.setOption("child.connectTimeoutMillis", 100);

	}

	private void initAcceptor(ServerBootstrap bootstrap, DefaultChannelGroup channelGroup, int port) {
		Channel acceptor = bootstrap.bind(new InetSocketAddress(port));
		if (acceptor.isBound()) {
			System.out.println("server started");
			channelGroup.add(acceptor);
		}
	}

}
