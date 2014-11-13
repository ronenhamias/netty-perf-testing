package io.netty.example.netty3.consistentload;

import io.netty.example.netty3.ValueLatch;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

public class ConsistentLoadHandler extends SimpleChannelUpstreamHandler {

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
	}
}
