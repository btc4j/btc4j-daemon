/*
 The MIT License (MIT)
 
 Copyright (c) 2013, 2014 by ggbusto@gmx.com

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */

package org.btc4j.daemon;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Logger;

public class BtcDaemonNotifier {
	private final static Logger LOG = Logger
			.getLogger(BtcDaemonNotifier.class.getName());
	private static final String BTC4J_DAEMON_HOST = "127.0.0.1";
	private static final int BTC4J_DAEMON_ALERT_PORT = 18336;
	private static final String BTC4J_DAEMON_NOTIFIER_USAGE = "usage: java BitcoinDaemonNotifier <host> <port> <message>";
	private static final String BTC4J_DAEMON_NOTIFIER_ERROR = "bitcoin daemon notifier error";

	public static void main(String[] args) {
		String host = BTC4J_DAEMON_HOST;
		int port = BTC4J_DAEMON_ALERT_PORT;
		String message = "";
		try {
			host = args[0].trim();
			port = Integer.parseInt(args[1]);
			message = args[2].trim();
		} catch (Throwable t) {
			LOG.warning(String.valueOf(t));
			System.err.println(BTC4J_DAEMON_NOTIFIER_USAGE);
			System.exit(1);
		}
		System.exit(notify(host, port, message));
	}

	public static int notify(String host, int port, String message) {
		int status = 0;
		try (Socket socket = new Socket(host, port);
				PrintWriter out = new PrintWriter(socket.getOutputStream(),
						true);) {
			out.println(message);
			LOG.info("sent notification: " + message + " to " + host + ":"
					+ port);
		} catch (Throwable t) {
			LOG.warning(BTC4J_DAEMON_NOTIFIER_ERROR + " " + host + ":" + port + " " + message + ": "+ t);
			status = 1;
		}
		return status;
	}
}
