package io.netty.example.netty4;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultMessageSizeEstimator;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author Anton Kharenko
 */
public class ServerFactory {

	public void bind(int port) {
		final ServerBootstrap bootstrap = new ServerBootstrap();
		
		EventLoopGroup group;
		if(System.getProperty("os.name").startsWith("Windows"))
			group = new NioEventLoopGroup();
		else
			group = new EpollEventLoopGroup();
		
		
		bootstrap.group( group)
				.channel(NioServerSocketChannel.class)
				.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
				.childOption(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(20))
				.childOption(ChannelOption.TCP_NODELAY, true)
				.childOption(ChannelOption.SO_REUSEADDR, true)
				.childOption(ChannelOption.MESSAGE_SIZE_ESTIMATOR, new DefaultMessageSizeEstimator(100))
				.childOption(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, 32 * 1024)
				.childOption(ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, 8 * 1024)
				.childHandler(new ServerInitializer());

		bootstrap.bind(port);
	}

}
