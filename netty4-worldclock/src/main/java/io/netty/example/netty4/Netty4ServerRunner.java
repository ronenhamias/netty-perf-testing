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

import io.netty.example.common.KPIReporter;
import io.netty.example.common.TPSCounter;

public final class Netty4ServerRunner {

    static final int PORT = Integer.parseInt(System.getProperty("port", "8787"));
   
    public static void main(String[] args) throws Exception {
		System.setProperty("io.netty.leakDetectionLevel","disabled");

		KPIReporter.start();
		ServerFactory server = new ServerFactory();
		server.bind(PORT);
    }
}
