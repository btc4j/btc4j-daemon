package org.btc4j.daemon;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Observable;
import java.util.logging.Logger;

public class BtcNotificationListener extends Observable implements Runnable {
	private static final Logger LOGGER = Logger
			.getLogger(BtcNotificationListener.class.getName());
	private int port;

	public BtcNotificationListener(int port) {
		this.port = port;
	}

	@Override
	public void run() {
		try (ServerSocket server = new ServerSocket(port);) {
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
				} catch (Throwable t) {
					LOGGER.warning(String.valueOf(t));
				}
			}
		} catch (Throwable t) {
			LOGGER.severe(String.valueOf(t));
		}
	}
}
