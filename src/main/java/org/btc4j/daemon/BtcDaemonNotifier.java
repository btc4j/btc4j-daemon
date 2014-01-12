/*
 The MIT License (MIT)
 
 Copyright (c) 2013, 2014 by Guillermo Gonzalez, btc4j.org

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
	private final static Logger LOGGER = Logger
			.getLogger(BtcDaemonNotifier.class.getName());

	public static void main(String[] args) {
		BtcNotificationType notification = BtcNotificationType.ALERT;
		int port = BtcDaemonConstant.BTC4J_DAEMON_NOTIFIER_PORT;
		String payload = "";
		try {
			notification = BtcNotificationType.getValue(args[0]);
			port = Integer.parseInt(args[1]);
			payload = args[2].trim();
		} catch (Throwable t) {
			LOGGER.warning(String.valueOf(t));
			System.err
					.println(BtcDaemonConstant.BTC4J_DAEMON_NOTIFIER_USAGE);
			System.exit(1);
		}
		try (Socket socket = new Socket(
				BtcDaemonConstant.BTC4J_DAEMON_HOST, port);
				PrintWriter out = new PrintWriter(socket.getOutputStream(),
						true);) {
			String message = notification + "|" + payload;
			out.println(message);
			LOGGER.info("sent notification: " + message + " to "
					+ BtcDaemonConstant.BTC4J_DAEMON_HOST + ":" + port);

		} catch (Throwable t) {
			LOGGER.severe(BtcDaemonConstant.BTC4J_DAEMON_NOTIFIER_ERROR + t);
			System.exit(1);
		}
		System.exit(0);
	}
}
