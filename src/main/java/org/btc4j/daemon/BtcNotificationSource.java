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
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Observable;
import java.util.logging.Logger;

public class BtcNotificationSource extends Observable implements Runnable {
	private static final Logger LOG = Logger
			.getLogger(BtcNotificationSource.class.getName());
	private int port;

	public BtcNotificationSource(int port) {
		this.port = port;
	}

	@Override
	public void run() {
		Thread currentThread = Thread.currentThread();
		try (ServerSocket server = new ServerSocket(port);) {
			LOG.info("thread " + currentThread.getName()
					+ " started server socket " + port);
			while (!currentThread.isInterrupted()) {
				LOG.info("thread " + currentThread.getName()
						+ " waiting to accept " + port);
				try (Socket socket = server.accept();
						BufferedReader in = new BufferedReader(
								new InputStreamReader(socket.getInputStream()));) {
					String line;
					while ((line = in.readLine()) != null) {
						LOG.info("thread " + currentThread.getName()
								+ " received " + line);
						setChanged();
						notifyObservers(line);
					}
				} catch (Throwable t) {
					LOG.warning(String.valueOf(t));
				}
			}
		} catch (Throwable t) {
			LOG.severe(String.valueOf(t));
		}
	}
}
