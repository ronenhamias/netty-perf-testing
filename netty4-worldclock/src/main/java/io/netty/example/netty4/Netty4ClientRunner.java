/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.example.netty4;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

public final class Netty4ClientRunner {

	static final String HOST = "127.0.0.1";
	static final int PORT = 8787;

	public static void main(String[] args) throws Exception {
		ClientFactory client = new ClientFactory();
		Channel channel = client.connect(HOST, PORT);

		BetterWrite write = new BetterWrite(channel);
		while (true) {
			for (int j = 0; j < 10; j++) {
				write.write(channel,"hello world\n",new ChannelFutureListener() {
					@Override
					public void operationComplete(ChannelFuture future) throws Exception {
						// TODO Auto-generated method stub
						
					}
				});
			}
			Thread.sleep(1);
		}
	}
}
