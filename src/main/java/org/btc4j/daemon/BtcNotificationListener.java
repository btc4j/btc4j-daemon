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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Observable;
import java.util.logging.Logger;

public class BtcNotificationListener extends Observable implements Runnable {
	private final static Logger LOGGER = Logger
			.getLogger(BtcNotificationListener.class.getName());
	@SuppressWarnings("unused")
	private BtcDaemon daemon;
	private ServerSocket server;

	public BtcNotificationListener(BtcDaemon daemon, ServerSocket server) {
		this.daemon = daemon;
		this.server = server;
	}

	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			try (Socket socket = server.accept();
					BufferedReader in = new BufferedReader(
							new InputStreamReader(socket.getInputStream()));) {
				String line;
				if ((line = in.readLine()) != null) {
					socket.close();
					LOGGER.info("received " + line);
					setChanged();
					notifyObservers(line);
				}
			} catch (IOException e) {
				LOGGER.warning(String.valueOf(e));
			}
		}
	}
}
