package io.netty.example.netty3;

import org.jboss.netty.channel.Channel;

public class Netty3ClientRunner {

	private static final String HOST = "127.0.0.1";
	private static final int PORT = 8787;

	public static void main(String[] args) throws InterruptedException {
		ClientFactory client = new ClientFactory();
		Channel channel = client.connect(HOST, PORT);

		while (true) {
			for (int j = 0; j < 10; j++) {
				channel.write("hello world\n");
			}
			Thread.sleep(1);
		}
	}

}
