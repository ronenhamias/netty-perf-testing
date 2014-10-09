package io.netty.example.netty3;

import io.netty.example.common.Counter;

public class Netty3ServerRunner {

	private static final int PORT = 8787;

	public static void main(String[] args) {
		Counter.start();
		ServerFactory server = new ServerFactory();
		server.bind(PORT);
	}

}
