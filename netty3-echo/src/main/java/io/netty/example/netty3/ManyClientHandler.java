package io.netty.example.netty3;

import org.jboss.netty.channel.*;

public class ManyClientHandler extends SimpleChannelUpstreamHandler {

	//	static final String helloRequestTemplate = "{\"qualifier\":\"pt.openapi.hello/sayHello\",\"contextId\":\"[%CONTEXT_ID%]\",\"data\":{\"name\":\"ronen\"}}";
	static final String helloRequestTemplate = "{\"qualifier\":\"pt.openapi.hello/echo16K\",\"contextId\":\"[%CONTEXT_ID%]\",\"data\":{\"name\":\"ronen\"}}";
	static final String heartbeatRequest = "{\"qualifier\":\"pt.openapi.context/heartbeatRequest\"}";

	String helloRequest;

	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) {
		setContextLatchNull(ctx);
		System.out.println("ManyClientHandler.channelClosed= " + e);
	}

	@Override
	public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
		setContextLatchNull(ctx);
		System.out.println("ManyClientHandler.channelDisconnected= " + e);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
		setContextLatchNull(ctx);
		System.out.println("ManyClientHandler.exceptionCaught=" + e.getCause());
	}

	@Override
	public void messageReceived(final ChannelHandlerContext ctx, MessageEvent e) {
		Channel channel = ctx.getChannel();
		String message = (String) e.getMessage();
		message = message.substring(4);
		if (message.contains("pt.openapi.context/createContextResponse")) {
			String contextId = message.subSequence(77, 97).toString();
			ValueLatch createContextLatch = (ValueLatch) channel.getAttachment();
			createContextLatch.setValue(contextId);
			channel.write(helloRequest = helloRequestTemplate.replace("[%CONTEXT_ID%]", contextId));
		}
		//		else if (message.contains("pt.openapi.hello/sayHelloResponse")) {
		else if (message.contains("pt.openapi.hello/echo16K")) {
			for (int i = 0; i < Netty3ManyClientsRunner.factor; i++) {
				channel.write(helloRequest);
			}
		}
		else if (message.contains("heartbeatNotification")) {
			channel.write(heartbeatRequest);
		}
		else {
			System.err.println("### Can't recognize received message:\n" + message);
		}
	}

	private void setContextLatchNull(ChannelHandlerContext ctx) {
		Channel channel = ctx.getChannel();
		ValueLatch createContextLatch = (ValueLatch) channel.getAttachment();
		if (createContextLatch != null) {
			createContextLatch.setValue(null);
		}
	}
}
