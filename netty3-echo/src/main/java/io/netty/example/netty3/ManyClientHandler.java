package io.netty.example.netty3;

import org.jboss.netty.channel.*;

public class ManyClientHandler extends SimpleChannelUpstreamHandler {

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
		Channel channel = ctx.getChannel();
		String message = (String) e.getMessage();
		message = message.substring(4);
		if (message.contains("pt.openapi.context/createContextResponse")) {
			String contextId = message.subSequence(77, 97).toString();
			ValueLatch<String> createContextLatch = (ValueLatch<String>) channel.getAttachment();
			createContextLatch.setValue(contextId);
		}
		else if (message.contains("pt.openapi.hello/sayHelloResponse")) {
			String helloRequest = (String) channel.getAttachment();
			for (int i = 0; i < Netty3ManyClientsRunner.FACTOR; i++) {
				channel.write(helloRequest);
			}
		}
	}
}
