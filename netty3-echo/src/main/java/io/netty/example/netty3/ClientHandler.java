package io.netty.example.netty3;

import org.jboss.netty.channel.*;


public class ClientHandler extends SimpleChannelUpstreamHandler {

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
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {

		String message =  (String) e.getMessage();
		message = message.substring(4);
		
		if(message.contains("pt.openapi.context/createContextResponse")){
			String contextId = message.subSequence(77, 97).toString();
			Netty3ClientRunner.latch.setValue(contextId);
		}
		else if(message.contains("pt.openapi.hello/sayHelloResponse/1.0")){
			Netty3ClientRunner.send(ctx.getChannel());
			Netty3ClientRunner.send(ctx.getChannel());
		}
			
	}

}

