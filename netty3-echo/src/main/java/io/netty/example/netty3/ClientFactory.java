package io.netty.example.netty3;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * @author Anton Kharenko
 */
public class ClientFactory {

	public Channel connect(String host, int port) throws InterruptedException {
		ClientBootstrap clientBootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));
		clientBootstrap.setPipelineFactory(new ClientPipelineFactory());
		ChannelFuture future = clientBootstrap.connect(new InetSocketAddress(host, port));
		return future.await().getChannel();
	}

}
