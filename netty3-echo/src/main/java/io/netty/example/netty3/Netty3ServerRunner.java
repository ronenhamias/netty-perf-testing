package io.netty.example.netty3;

import io.netty.example.common.KPIReporter;
import io.netty.example.common.TPSCounter;

public class Netty3ServerRunner {

	private static final int PORT = 8787;

	public static void main(String[] args) {
		KPIReporter.start();
		ServerFactory server = new ServerFactory();
		server.bind(PORT);
	}

}
