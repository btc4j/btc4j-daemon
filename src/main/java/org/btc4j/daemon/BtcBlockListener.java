package org.btc4j.daemon;

import java.util.logging.Logger;

import org.btc4j.core.BtcBlock;

public class BtcBlockListener extends BtcNotificationListener {
	private static final Logger LOGGER = Logger
			.getLogger(BtcBlockListener.class.getName());
	private BtcDaemon daemon;
	
	public BtcBlockListener(int port, BtcDaemon daemon) {
		super(port);
		this.daemon = daemon;
	}
	
	@Override
	public void notifyObservers(Object arg) {
		if (hasChanged()) {
			String hash = String.valueOf(arg).trim();
			BtcBlock block;
			try {
				block = daemon.getBlock(hash);
			} catch (Throwable t) {
				LOGGER.warning(String.valueOf(t));
				block = new BtcBlock();
				block.setHash(hash);
			}
			super.notifyObservers(block);
		}
	}
}
