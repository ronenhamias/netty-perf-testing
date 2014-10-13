package io.netty.example.netty3;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.LengthFieldPrepender;
import org.jboss.netty.handler.codec.frame.LineBasedFrameDecoder;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;

public class ClientPipelineFactory implements ChannelPipelineFactory {

	@Override
	public ChannelPipeline getPipeline() throws Exception {
		ChannelPipeline pipeline = Channels.pipeline();
		pipeline.addLast("prepender", new LengthFieldPrepender(4));
		
		pipeline.addLast("decoder", new StringDecoder());
		
		pipeline.addLast("framer", new LengthFieldBasedFrameDecoder(1024*2, 0, 4));
		
		pipeline.addLast("encoder", new StringEncoder());
		pipeline.addLast("handler", new ClientHandler());
		return pipeline;
	}

}