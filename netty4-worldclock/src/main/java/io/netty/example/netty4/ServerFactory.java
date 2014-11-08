package io.netty.example.netty4;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultMessageSizeEstimator;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.example.common.OSValidator;

import java.util.concurrent.Executors;
/**
 * @author Anton Kharenko
 */
public class ServerFactory {

	private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Class channelClass = NioServerSocketChannel.class;
    
    ExecutorService boss = new ForkJoinPool(8);
    ExecutorService worker = Executors.newCachedThreadPool();
	protected void initGroups() {
        if (OSValidator.isUnix()) {
            bossGroup = new EpollEventLoopGroup(Runtime.getRuntime().availableProcessors()*4);
            workerGroup = new EpollEventLoopGroup(Runtime.getRuntime().availableProcessors()*4);
            channelClass =  EpollServerSocketChannel.class;
        } else {
            bossGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors(),boss);
            workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors()*2,worker);
        }
    }
	
	public void bind(int port) {
		initGroups();
		final ServerBootstrap bootstrap = new ServerBootstrap();

		bootstrap.group( bossGroup,workerGroup)
				.channel(channelClass)
				.childOption(ChannelOption.TCP_NODELAY, true)
				.childOption(ChannelOption.SO_REUSEADDR, true)
				.childOption(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, 32 * 1024)
				.childOption(ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, 8 * 1024)
				.childHandler(new ServerInitializer());

		bootstrap.bind(port);
	}

}
