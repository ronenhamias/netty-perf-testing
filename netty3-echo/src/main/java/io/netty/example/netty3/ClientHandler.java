package io.netty.example.netty3;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.jboss.netty.channel.*;


public class ClientHandler extends SimpleChannelUpstreamHandler {
	int fac = Integer.parseInt(Netty3ClientRunner.FACTOR);
	Executor exec = Executors.newCachedThreadPool();

	@Override
	public void channelBound(ChannelHandlerContext ctx, ChannelStateEvent e) {
	}

	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
	}

	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) {
	}

	@Override
	public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
	}

	@Override
	public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) {
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
	}

	@Override
	public void messageReceived(final ChannelHandlerContext ctx, MessageEvent e) {

		String message =  (String) e.getMessage();
		message = message.substring(4);

		if(message.contains("pt.openapi.context/createContextResponse")){
			String contextId = message.subSequence(77, 97).toString();
			Netty3ClientRunner.latch.setValue(contextId);
		}
		else if(message.contains("pt.openapi.hello/sayHelloResponse")){
			for(int i=0; i< fac; i++){
				Netty3ClientRunner.send(ctx.getChannel());	
			}
		}

	}

}

